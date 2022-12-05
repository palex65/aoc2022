
class Stack {
    private val data = mutableListOf<Char>()
    fun push(e: Char) { data.add(e) }
    fun pop() = data.removeLast()
    fun top() = data.last()
    fun moveTo(n: Int, to: Stack) {
        List(n){ pop() }.reversed().forEach { to.push(it) }
    }
}

private fun List<String>.toStacks(): List<Stack> {
    val lines = dropLast(1).map{ it.chunked(4).map{ it[1] } }
    val stacks = List(lines.last().size){ Stack() }
    lines.reversed().forEach { it.forEachIndexed { idx, box ->
        if (box!=' ') stacks[idx].push(box)
    } }
    return stacks
}

private val movePattern = Regex("""move (\d+) from (\d+) to (\d+)""")

private fun partN(lines: List<String>, action: (n: Int, from: Stack, to: Stack) -> Unit): String {
    val (stkLines, movesLines) = lines.splitBy { it.isBlank() }
    val stacks = stkLines.toStacks()
    movesLines.forEach {
        val (n, from, to) = (movePattern.find(it) ?: error("invalid move $it"))
            .destructured.toList()
            .map { it.toInt() }
        action(n, stacks[from-1], stacks[to-1])
    }
    return stacks.joinToString(separator = "") { it.top().toString() }
}

private fun part1(lines: List<String>) = partN(lines,
    action = { n, from, to -> repeat(n) { to.push( from.pop() ) } }
)

private fun part2(lines: List<String>) = partN(lines,
    action = { n, from, to -> from.moveTo(n,to) }
)

fun main() {
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))  // ZBDRNPMVH
    println(part2(input))  // WDLPFNNNB
}
