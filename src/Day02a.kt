fun calc(o: Int, r: Int): Int = r + 1 + when(r - o) {
        -2,1 -> 6           // Win
        2,-1 -> 0           // Lose
        else /*0*/ -> 3      // Draw
    }

fun part1a(lines: List<String>): Int = lines.map { calc(it[0]-'A', it[2]-'X') }.sum()

fun part2a(lines: List<String>): Int = lines.map {
        val o = it[0] - 'A'
        calc(o, when (it[2]) {
            'X' -> (o + 2) % 3
            'Z' -> (o + 1) % 3
            else /*'Y'*/ -> 0
        } )
    }.sum()

fun main() {
    val testInput = readInput("Day02_test")
    check(part1a(testInput) == 15)
    check(part2a(testInput) == 12)

    val input = readInput("Day02")
    println(part1a(input))  // 14375
    println(part2a(input))  // 10274
}