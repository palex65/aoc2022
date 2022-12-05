
private fun String.toRanges() = indexOf(',').let { line ->
    fun String.toRange() = indexOf('-').let { substring(0,it).toInt() .. substring(it+1).toInt() }
    substring(0,line).toRange() to substring(line+1).toRange()
}

operator fun IntRange.contains(b: IntRange) = b.first>=first && b.last<=last

private fun part1(lines: List<String>): Int =
    lines.count {
        val(a, b) = it.toRanges()
        a in b || b in a
    }

private fun part2(lines: List<String>): Int =
    lines.count {
        val (a, b) = it.toRanges()
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
