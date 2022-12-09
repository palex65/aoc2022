import kotlin.math.abs

private data class Position(val x: Int, val y: Int)

private enum class Direction(val dx: Int, val dy: Int){
    LEFT(-1,0), RIGHT(+1,0), UP(0,+1), DOWN(0,-1)
}
private fun Char.toDir() = Direction.values().first{ this == it.name[0] }

private operator fun Position.plus(d: Direction) = Position(x+d.dx, y+d.dy)

private fun Position.follow(p: Position): Position {
    val difX = p.x-x
    val difY = p.y-y
    return if (abs(difX) > 1 || abs(difY) > 1)
        Position( x + difX.coerceIn(-1..1), y + difY.coerceIn(-1..1) )
    else this
}

private fun tailVisits(input: List<String>, nodes: Int=0) = buildSet {
    var head = Position(0,0)
    val middle = MutableList(nodes){ Position(0,0) }
    var tail = Position(0,0)
    input.forEach {
        val dir = it.substringBefore(' ')[0].toDir()
        val steps = it.substringAfter(' ').toInt()
        repeat(steps) {
            head += dir
            if (nodes>0) {
                middle[0] = middle[0].follow(head)
                for(i in 1..middle.lastIndex) middle[i] = middle[i].follow(middle[i-1])
                tail = tail.follow(middle[nodes-1])
            } else
                tail = tail.follow(head)
            add(tail)
        }
    }
}

private fun part1(input: List<String>): Int =
    tailVisits(input).size

private fun part2(input: List<String>): Int =
    tailVisits(input,8).size

fun main() {
    val input_test = readInput("Day09_test")
    check(part1(input_test) == 13)
    check(part2(input_test) == 1)
    val input_test1 = readInput("Day09_test1")
    check(part2(input_test1) == 36)

    val input = readInput("Day09")
    println(part1(input))  // 6406
    println(part2(input))  // 2643
}
