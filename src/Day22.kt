@file:Suppress("PackageDirectoryMismatch")
package day22
import readInput
import splitBy
import day22.Tile.*
import day22.Dir.*

data class Pos(val row: Int, val col: Int)
enum class Tile(val char: Char) { OPEN('.'), SOLID('#') }
typealias Field = Map<Pos,Tile>

fun Char.toTile() = Tile.values().firstOrNull { this==it.char }

fun Field(ls: List<String>): Field = buildMap {
    ls.forEachIndexed { row, line ->
        line.forEachIndexed { col, c ->
            c.toTile()?.let { set(Pos(row, col), it) }
        }
    }
}

fun Field.leftMost() = Pos( 0, keys.filter { it.row==0 }.minOf { it.col } )

sealed interface Action
class Move(val tiles: Int): Action
enum class Turn: Action { Left, Right }
typealias Path = List<Action>

fun Path(s: String): Path = buildList {
    var idx = 0
    while(idx < s.length) {
        var c = s[idx++]
        when{
            c == 'L' -> add( Turn.Left )
            c == 'R' -> add( Turn.Right )
            c.isDigit() -> {
                var tiles = c.digitToInt()
                while ( idx < s.length && s[idx].also { c=it }.isDigit() ) {
                    tiles = tiles * 10 + c.digitToInt()
                    ++idx
                }
                add( Move(tiles) )
            }
            else -> error("Invalid path symbol $c")
        }
    }
}

enum class Dir(val dRow: Int, val dCol: Int) {
    RIGHT(0,+1), DOWN(+1,0), LEFT(0,-1), UP(-1,0);
}
fun Char.toDir() = Dir.values().first { it.name[0]==this }

fun Dir.turn(t: Turn): Dir = when (t) {
    Turn.Right -> next()
    Turn.Left -> prev()
}

fun Dir.inverted() = Dir.values()[ (ordinal+2) % Dir.values().size ]
fun Dir.next() = Dir.values()[ (ordinal+1) % Dir.values().size ]
fun Dir.prev() = Dir.values()[ (ordinal-1).mod(Dir.values().size) ]

operator fun Pos.plus(dir: Dir) = Pos( row+dir.dRow, col+dir.dCol)

fun Field.wrap(pos: Pos, dir: Dir): Pos = when(dir) {  // Part1
    RIGHT -> keys.filter { it.row == pos.row }.minBy { it.col }
    LEFT -> keys.filter { it.row == pos.row }.maxBy { it.col }
    UP -> keys.filter { it.col == pos.col }.maxBy { it.row }
    DOWN -> keys.filter { it.col == pos.col }.minBy { it.row }
}

fun Links.wrap(from: Pos, dir: Dir): Pair<Pos,Dir> {
    val fromSide = Side((from.row / dim) * 4 + (from.col / dim), dir)
    val toSide = wrap[fromSide]
    checkNotNull(toSide)
    val toRow = toSide.face / 4 * dim + when(toSide.dir) {
        UP -> 0
        DOWN -> dim-1
        else -> when(toSide.dir) {
            dir.inverted() -> from.row % dim
            dir -> dim-1 - from.row % dim
            dir.prev() -> from.col % dim
            else -> dim-1 - from.col % dim
        }
    }
    val toCol = toSide.face % 4 * dim + when(toSide.dir) {
        LEFT -> 0
        RIGHT -> dim-1
        else -> when(toSide.dir) {
            dir.inverted() -> from.col % dim
            dir -> dim-1 - from.col % dim
            dir.next() -> from.row % dim
            else -> dim-1 - from.row % dim
        }
    }
    return Pos(toRow,toCol) to toSide.dir.inverted()
}

fun solve(field: Field, path: Path, links: Links): Int {
    var position = field.leftMost()
    var dir = RIGHT

    fun step(tiles: Int) {
        repeat(tiles) {
            var nextPos = position+dir
            var tile = field[nextPos]
            if( tile == null ) {
                val next = links.wrap(position,dir)
                nextPos = next.first
                tile = field[nextPos]
                if (tile==OPEN) dir = next.second
            }
            if (tile == SOLID) return
            position = nextPos
        }
    }
    for(action in path) {
        when(action) {
            is Turn -> dir = dir.turn(action)
            is Move -> step( action.tiles )
        }
    }
    return 1000 * (position.row+1) + 4 * (position.col+1) + dir.ordinal
}

data class Side(val face: Int, val dir: Dir) { init { check(face in 0..15) } }
data class Links(val dim: Int, val wrap: Map<Side,Side>) { init { check(dim==4 || dim==50) } }

fun Links(dim: Int, s: String): Links {
    val direct = s.split(' ').map { Side(it[0].digitToInt(16), it[1].toDir()) to Side(it[3].digitToInt(16), it[4].toDir()) }
    val reverse = direct.map { (fromSide,toSide) -> toSide to fromSide }
    return Links(dim, (direct+reverse).toMap() )
}

fun parse(lines: List<String>): Pair<Field,Path> {
    val (f,p) = lines.splitBy { it.isEmpty() }
    return Field(f) to Path(p.first())
}

fun main() {
    val (testField, testPath) = parse(readInput("Day22_test"))
    check(solve(testField, testPath, Links(4,"2R-2L 2U-AD 4D-4U 4L-6R 5U-5D AL-BR BU-BD")) == 6032)
    check(solve(testField, testPath, Links(4,"2L-5U 2U-4U 2R-BR 4L-BD 4D-AD 5D-AL 6R-BU")) == 5031)

    val (field, path) = parse(readInput("Day22"))
    println(solve(field, path, Links(50,"1L-2R 1U-9D 2U-2D 5L-5R 8L-9R 8U-CD CL-CR")))  // 80392
    println(solve(field, path, Links(50,"1L-8L 1U-CL 2U-CD 2R-9R 2D-5R 5L-8U 9D-CR")))  // 19534
}
