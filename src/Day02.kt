
enum class Play(val first: Char, val part1Second: Char) {
    Rock('A','X'),
    Paper('B','Y'),
    Scissors('C','Z');  // More plays can be added, in order

    val score = ordinal+1
    val winner get() = values()[(ordinal + 1) % values().size]
    val loser get() = values()[(ordinal - 1 + values().size) % values().size]
}
fun Char.toPlay(responseCode: Boolean=false) =
    Play.values().first { this == if (responseCode) it.part1Second else it.first }

enum class RoundResult(val score:Int, val part2Symbol: Char, val toResponse: (opponent: Play) -> Play) {
    Win(6,'Z', { it.winner } ),
    Draw(3,'Y', { it } ),
    Lose(0,'X', { it.loser })
}
fun Char.toResult() = RoundResult.values().first { this == it.part2Symbol }

data class Round(val opponent: Play, val response: Play) {
    private fun toResult() = RoundResult.values().first { response == it.toResponse(opponent) }
    fun toScore() =
        response.score + toResult().score
}

fun String.toRound( response: (symbol: Char, opponent: Play) -> Play ): Round {
    require(length == 3 && this[1] == ' ')
    val opponent = this[0].toPlay()
    return Round(opponent, response(this[2], opponent))
}

private fun part1(lines: List<String>): Int =
    lines.sumOf { it.toRound { symbol, _ -> symbol.toPlay(responseCode = true) }.toScore() }

private fun part2(lines: List<String>): Int =
    lines.sumOf { it.toRound { symbol, opponent -> symbol.toResult().toResponse(opponent) }.toScore() }

fun main() {
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))  // 14375
    println(part2(input))  // 10274
}
