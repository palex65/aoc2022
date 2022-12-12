
private data class Pos(val x:Int, val y: Int)

private fun Pos.neighbors() = listOf( Pos(x-1,y), Pos(x+1,y), Pos(x,y-1), Pos(x,y+1) )
private fun Pos.validNeighbors(a: Area) = neighbors().filter { a.isValid(it) }

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
            line.forEachIndexed { x, l ->
                if (l==level) add(Pos(x,y))
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

private data class Node(val pos: Pos, val steps: List<Pos>)

private fun part1(input: List<String>) =
    partN(input.posOf('S'), input.posOf('E'), Area(input))

private fun part2(input: List<String>): Int {
    val area = Area(input)
    val end = input.posOf('E')
    return area.allPosOf(0).map { partN(it, end, area) }.filter { it>0 }.min()
}

private fun partN(start: Pos, end: Pos, area: Area): Int {
    val visited = mutableMapOf<Pos, List<Pos>>()
    val open = mutableListOf(Node(start, emptyList()))
    while (open.isNotEmpty()) {
        val (pos, steps) = open.removeFirst()
        visited[pos] = steps
        val nextSteps = steps + pos
        pos.validNeighbors(area)
            .filter {
                val nl = area[it]; val l = area[pos]
                nl == l + 1 || nl <= l
            }
            .forEach { p ->
                val vSteps = visited[p]
                if ((vSteps == null || vSteps.size > nextSteps.size) && open.none{ it.pos==p } )
                    open.add(Node(p, nextSteps))
            }
    }
    return visited[end]?.size ?: -1
}

fun main() {
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))  // 437
    println(part2(input))  // 430
}
