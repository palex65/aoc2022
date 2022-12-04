
private fun String.toRanges(): List<IntRange> = split(',').map { range ->
    val (start, end) = range.split('-').map { it.toInt() }
    start..end
}

private fun part1(lines: List<String>): Int =
    lines.count { line ->
        val(a, b) = line.toRanges()
        a.all{ it in b } || b.all { it in a }
    }

private fun part2(lines: List<String>): Int =
    lines.count { line ->
        val (a, b) = line.toRanges()
        a.any { it in b }
    }

fun main() {
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))  // 588
    println(part2(input))  // 911
}
