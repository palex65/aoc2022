
fun Char.toPriority() = when (this) {
    in 'a'..'z' -> this-'a'+1
    in 'A'..'Z' -> this-'A'+27
    else -> error("Invalid item $this")
}

private fun part1(lines: List<String>) = lines.sumOf { l ->
    val a = l.substring(0, l.length / 2)
    val b = l.substring(l.length / 2)
    a.first { it in b }.toPriority()
}

private fun part2(lines: List<String>) = lines.chunked(3).sumOf { (a, b, c) ->
    a.first { it in b && it in c }.toPriority()
}

fun main() {
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))  // 7917
    println(part2(input))  // 2585
}
