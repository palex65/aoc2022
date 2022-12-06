
private fun partN(lines: List<String>, n: Int): Int {
    val line = lines.first()
    line.toList().windowed(n,1,false).forEachIndexed { idx, win ->
        if (win.distinct().size==win.size) return idx+n
    }
    return -1
}

private fun part1(lines: List<String>) = partN(lines,4)

private fun part2(lines: List<String>)= partN(lines,14)

fun main() {
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 26)

    val input = readInput("Day06")
    println(part1(input))  // 1896
    println(part2(input))  // 3452
}
