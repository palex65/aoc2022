private typealias CPU = List<Int> // Values of register X at each cycle

private fun execProgram(lines: List<String>): CPU = buildList {
    var regX = 1
    fun tick() {
        add(regX)
    }
    for (line in lines) {
        when (val op = line.substringBefore(' ')) {
            "noop" -> tick()
            "addx" -> {
                tick(); tick()
                regX += line.substringAfter(' ').toInt()
            }
        }
    }
}

private fun part1(lines: List<String>): Int =
    execProgram(lines)
        .mapIndexed { idx, reg ->
            val cycle = idx + 1
            if (cycle % 40 == 20) cycle * reg else 0
        }
        .sum()

private fun part2(lines: List<String>): List<String> =
    execProgram(lines)
        .mapIndexed { idx, x ->
            val i = idx % 40
            if (i in (x - 1)..(x + 1)) '#' else '.'
        }
        .chunked(40)
        .map { it.joinToString(separator = "") }


fun main() {
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    check(part2(testInput) == listOf(
        "##..##..##..##..##..##..##..##..##..##..",
        "###...###...###...###...###...###...###.",
        "####....####....####....####....####....",
        "#####.....#####.....#####.....#####.....",
        "######......######......######......####",
        "#######.......#######.......#######.....",
    ))

    val input = readInput("Day10")
    println(part1(input))  // 12740
    part2(input).forEach { println(it) } // RBPARAGF
}
