@file:Suppress("PackageDirectoryMismatch")
package day23
import readInput
import day23.Dir.*

//data class Pos(val row: Int, val col: Int)

class Pos private constructor(private val index: Int) {
    val col get() = index % 256 - 50
    val row get() = index / 256 - 50
    override fun toString() = "($row,$col)"
    override fun equals(other: Any?) = (other as Pos).index == index
    override fun hashCode() = index
    companion object {
        private val cache = MutableList<Pos?>(256*256){ null }
        operator fun invoke(row: Int, col: Int): Pos {
            val index = (row+50) * 256 + (col+50)
            return cache[index] ?: Pos(index).also { cache[index] = it }
        }
    }
}

typealias Plant = Map<Pos,Pos?>

fun Plant.print(label: String = "---") {
    println("== $label ==")
    for(row in minOf { it.key.row } .. maxOf { it.key.row }) {
        for(col in minOf { it.key.col } .. maxOf { it.key.col })
            print( if(contains(Pos(row,col))) '#' else '.' )
        println()
    }
    println()
}

fun Plant.countGround(): Int {
    var count = 0
    val minRow = minOf { it.key.row }
    val maxRow = maxOf { it.key.row }
    val minCol = minOf { it.key.col }
    val maxCol = maxOf { it.key.col }
    //println("row in $minRow..$maxRow , col in $minCol..$maxCol")
    for(row in minRow..maxRow)
        for(col in minCol..maxCol)
            if(!contains(Pos(row,col))) ++count
    return count
}

fun Plant(ls: List<String>): Plant = buildMap {
    ls.forEachIndexed { row, line -> line.forEachIndexed{ col, c ->
        if (c=='#') this[Pos(row, col)] = null
    } }
}

enum class Dir(val dr: Int, val dc: Int) {
    N(-1,0), NE(-1,1), E(0,1), SE(1,1), S(1,0), SW(1,-1), W(0,-1), NW(-1,-1)
}
operator fun Pos.plus(dir: Dir) = Pos(row + dir.dr, col + dir.dc)

val steps = listOf( listOf(N,NE,NW), listOf(S,SE,SW), listOf(W,NW,SW), listOf(E,NE,SE) )

fun Plant.proposedMove(round:Int, pos:Pos): Pos? {
    repeat(steps.size) { t ->
        val step = steps[(round+t) % steps.size]
        if (step.map{ pos + it }.all { !contains(it) })
            return pos + step[0]
    }
    return null
}

fun round(round: Int, elves: MutableMap<Pos, Pos?>): Boolean {
    elves.forEach { (pos, _) ->
        elves[pos] =
            if (values().map { pos + it }.all { !elves.contains(it) }) null
            else elves.proposedMove(round, pos)
    }
    val toMove = elves.filter { (pos, propose) -> propose != null && elves.none { (p, v) -> p != pos && v == propose } }
    for ((pos, propose) in toMove) {
        elves.remove(pos)
        elves[propose!!] = null
    }
    return toMove.isNotEmpty()
}

fun part1(lines: List<String>): Int {
    val elves = Plant(lines).toMutableMap()
    //elves.print("Initial State")
    repeat(10){ round ->
        round(round, elves)
        //elves.print("End of round ${round+1}")
    }
    return elves.countGround()
}

fun part2(lines: List<String>): Int {
    val elves = Plant(lines).toMutableMap()
    var round = 0
    do {
        val hasMoves = round(round++, elves)
        //println("Round $round")
    } while (hasMoves)
    //elves.print("No moves round= $round")
    return round
}

fun main() {
    val testInput = readInput("Day23_test")
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    println(part1(input))  // 4049
    println(part2(input))  // 1021
}
