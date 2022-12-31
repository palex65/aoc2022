@file:Suppress("PackageDirectoryMismatch")
package day24
import readInput
import day24.Dir.*
import java.util.Comparator
import java.util.PriorityQueue
import kotlin.math.*

data class Pos(val row: Int, val col: Int) {
    override fun toString() = "($row,$col)"
}
data class Offset(val dRow: Int, val dCol: Int)
fun Offset.distance() = abs(dRow)+abs(dCol)

operator fun Pos.plus(off: Offset) = Pos(row + off.dRow, col + off.dCol)
operator fun Pos.minus(pos: Pos) = Offset(row - pos.row, col - pos.col)
fun Pos.mod(rows: Int, cols: Int) = Pos( row.mod(rows), col.mod(cols) )
fun Pos.inArea(rows: Int, cols: Int) = row in 0 until rows && col in 0 until cols

enum class Dir(val offset: Offset, val symbol: Char) {
    RIGHT(0,+1,'>'), DOWN(+1,0,'v'), LEFT(0,-1,'<'), UP(-1,0,'^');
    constructor(dRow: Int, dCol: Int, symbol: Char): this( Offset(dRow,dCol), symbol)
}
fun Char.toDir() = Dir.values().first { it.symbol==this }

operator fun Pos.plus(dir: Dir) = this + dir.offset

data class Blizzard(val pos: Pos, val dir: Dir)
typealias Blizzards = List<Blizzard>

fun Blizzards(lines: List<String>): Blizzards = buildList {
    lines.forEachIndexed { row, line -> line.forEachIndexed { col, c ->
        if (c in ">v<^") add(Blizzard( Pos(row-1,col-1), c.toDir()))
    } }
}

data class Context(
    val rows: Int,
    val cols: Int,
    val start: Pos,
    val target: Pos,
    val blizzards: MutableList<List<Blizzard>>,
)

data class State(
    val minute: Int,
    val expedition: Pos,
)

fun State.print(ctx: Context) {
    println("== Minute $minute ==")
    for(r in 0 until ctx.rows)
        println( (0 until ctx.cols).joinToString(""){ c ->
            val pos = Pos(r,c)
            val bs = ctx.blizzards[minute].filter { it.pos == pos }
            when {
                bs.size == 1 -> bs.first().dir.symbol
                bs.size > 1 -> bs.size.toString()[0]
                expedition == pos -> 'E'
                else -> '.'
            }.toString()
        } )
}

fun State.doStep(ctx: Context): List<State> = buildList {
    val min = minute+1
    val bliz = if (min==ctx.blizzards.size)
        ctx.blizzards[minute].map{ it.copy(pos = (it.pos + it.dir).mod(ctx.rows, ctx.cols)) }.also { ctx.blizzards.add(it) }
    else ctx.blizzards[min]
    Dir.values().map { dir -> expedition + dir }.forEach{ exp ->
        if ((exp.inArea(ctx.rows, ctx.cols)||exp==ctx.target) && bliz.none { it.pos == exp })
            add( State(min, exp))
    }
    if (bliz.none { it.pos == expedition })
        add( State(min,expedition))
}

fun Context.compareStates(s1: State, s2: State): Int {
    val d2 = (target - s2.expedition).distance()
    val d1 = (target - s1.expedition).distance()
    return d1-d2
}

fun solve(ctx: Context): Int {
    val visited = mutableSetOf<State>()
    val open = PriorityQueue(Comparator<State> { s1, s2 -> ctx.compareStates(s1,s2) })
    open.add( State(0, ctx.start) )
    var bestMinutes = Int.MAX_VALUE
    while (open.isNotEmpty()) {
        val state = open.remove()
        if (state.minute + (ctx.target-state.expedition).distance() > bestMinutes)
            continue
        visited.add(state)
        if (state.expedition==ctx.target)
            bestMinutes = min(state.minute,bestMinutes)
        else
            for (s in state.doStep(ctx))
                if (s !in open && s !in visited)
                    open.add(s)
    }
    return bestMinutes
}

fun Context(lines: List<String>): Context {
    val rows = lines.size-2
    val cols = lines[0].length-2
    return Context( rows, cols,
        start = Pos(row = -1, col= 0),
        target = Pos(row = rows, col = cols - 1),
        blizzards = mutableListOf(Blizzards(lines)),
    )
}

fun part1(lines: List<String>) = solve( Context(lines) )

fun part2(lines: List<String>): Int {
    val ctx = Context(lines)
    val toGoal = solve(ctx)
    val ctx2 = ctx.copy(start = ctx.target, target = ctx.start, blizzards = mutableListOf( ctx.blizzards[toGoal]))
    val toBack = solve( ctx2 )
    val toGoalAgain = solve( ctx.copy( blizzards = mutableListOf( ctx2.blizzards[toBack]) ) )
    return toGoal + toBack + toGoalAgain
}

fun main() {
    val testInput = readInput("Day24_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    println(part1(input))  // 314
    println(part2(input))  // 896
}
