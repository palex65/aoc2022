
private data class Pos(val x:Int, val y: Int)

private fun Pos.neighbors() = listOf( Pos(x-1,y), Pos(x+1,y), Pos(x,y-1), Pos(x,y+1) )

private class Area(input: List<String>) {
    val area = input.map { it.map { c ->
        when(c) {
            'S' -> 0
            'E' -> 'z'-'a'
            else -> c-'a'
        }
    } }
    operator fun get(p: Pos) = area[p.y][p.x]
    fun isValid(p: Pos) = p.y in (0..area.lastIndex) && p.x in (0..area[0].lastIndex)
    fun allPosOf(level: Int) = buildList {
        area.forEachIndexed { y, line ->
            line.forEachIndexed { x, lev ->
                if (lev==level) add(Pos(x,y))
            }
        }
    }
}

private fun List<String>.posOf(c: Char): Pos {
    forEachIndexed { y, line ->
        val x = line.indexOf(c)
        if (x!=-1) return Pos(x,y)
    }
    error("Position not found for $c")
}

private data class Node(val pos: Pos, val steps: Int)

private fun part1(input: List<String>) =
    shortestPath(input.posOf('S'), input.posOf('E'), Area(input))

private fun part2(input: List<String>): Int {
    val area = Area(input)
    val end = input.posOf('E')
    return area.allPosOf(0).mapNotNull { shortestPath(it, end, area) }.min()
}

private fun shortestPath(start: Pos, end: Pos, area: Area): Int? {
    val visited = mutableMapOf<Pos, Int>()
    val open = mutableListOf(Node(start, 0))
    while (open.isNotEmpty()) {
        val (pos, steps) = open.removeFirst()
        if (pos==end) return steps
        visited[pos] = steps
        pos.neighbors()
            .filter { area.isValid(it) && area[it] <= area[pos] + 1 }
            .forEach { p ->
                if (p !in visited && open.none { it.pos == p } )
                    open.add(Node(p, steps+1))
            }
    }
    return null
}

fun main() {
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))  // 437
    println(part2(input))  // 430
}
