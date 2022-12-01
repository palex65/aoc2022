
fun List<String>.toCalories() =
    splitBy { it.isEmpty() }                // List<List<String>>
    .map { it.map { it.toInt() }.sum() }    // List<Int>

fun part1(calories: List<Int>) = calories.max()

fun part2(calories: List<Int>) = calories.sorted().takeLast(3).sum()

fun main() {
    val testCalories = readInput("Day01_test").toCalories()
    check(part1(testCalories) == 24000)
    check(part2(testCalories) == 45000)

    val calories = readInput("Day01").toCalories()
    println(part1(calories))  // 69795
    println(part2(calories))  // 208437
}
