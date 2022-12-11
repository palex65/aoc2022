
private fun createOperation(txt: String): (Long) -> Long {
    val (a, op, b) = txt.substringAfter("new = ").split(' ')
    check(a=="old")
    val const = b.toIntOrNull()
    return when(op[0]){
        '+' -> if (const==null) { old -> old + old } else { old -> old + const }
        '*' -> if (const==null) { old -> old * old } else { old -> old * const }
        else -> error("Invalid operation $op")
    }
}

private class Monkey(input: List<String>) {
    var items = input[0].split(", ").map { it.toInt() }.toMutableList()
    val operation = createOperation(input[1])
    val modulo = input[2].substringAfter("divisible by ").toInt()
    val ifTrue = input[3].substringAfter("throw to monkey ").toInt()
    val ifFalse = input[4].substringAfter("throw to monkey ").toInt()
    var inspected = 0

    fun action(monkeys: List<Monkey>, divideBy: Int, mod: Int) {
        items.forEach {
            val level = (( operation(it.toLong()) / divideBy ) % mod).toInt()
            monkeys[if (level % modulo == 0) ifTrue else ifFalse]
                .items.add(level)
        }
        inspected += items.size
        items.clear()
    }
}

private fun go(lines: List<String>, divideBy: Int, rounds: Int): Long {
    val monkeys = lines.splitBy { it.isBlank() }.map{ Monkey(it.drop(1).map { ln -> ln.substringAfter(": ") }) }
    val mod = monkeys.map { it.modulo }.reduce(Int::times)
    repeat(rounds) {
        monkeys.forEach { it.action(monkeys,divideBy,mod) }
    }
    val (max1,max2) = monkeys.map { it.inspected }.sortedDescending()
    return max1.toLong() * max2
}

private fun part1(lines: List<String>) = go(lines, 3, 20)

private fun part2(lines: List<String>) = go(lines, 1, 10000)

fun main() {
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158)
    val input = readInput("Day11")
    println(part1(input))  // 64032
    println(part2(input))  // 12729522272
}
