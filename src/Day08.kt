
fun <R> List<String>.mapEachCharIndexed(transform: (row: Int, col: Int, char: Char)->R ): List<R> {
    val res = mutableListOf<R>()
    for(row in indices) {
        val line = get(row)
        for (col in line.indices)
            res.add(transform(row, col, line[col]))
    }
    return res
}

private fun part1(grid: List<String>) =
    grid.mapEachCharIndexed { row, col, tree ->
        row == 0 || col == 0 || row == grid.lastIndex || col == grid.first().lastIndex ||   // around
        (col + 1..grid[row].lastIndex).all { grid[row][it] < tree } ||                      // right
        (col - 1 downTo 0).all { grid[row][it] < tree } ||                                  // left
        (row + 1..grid.lastIndex).all { grid[it][col] < tree } ||                           // down
        (row - 1 downTo 0).all { grid[it][col] < tree }                                     // up
    }.count { it }

private fun List<String>.scenicScore(l: Int, c: Int, height: Char): Int {
    if (l==0 || c==0 || l==lastIndex || c==first().lastIndex)
        return 0
    fun IntProgression.countWhile( getter: (Int)->Char ): Int {
        var counter = 0
        for (i in this)
            if (getter(i)<height) counter++
            else { counter++; break }
        return counter
    }
    val right = (c + 1..this[l].lastIndex).countWhile { this[l][it] }
    val left = (c - 1 downTo 0).countWhile { this[l][it] }
    val down = (l + 1..lastIndex).countWhile { this[it][c] }
    val up = (l - 1 downTo 0).countWhile { this[it][c] }
    return right * left * down * up
}

private fun part2(grid: List<String>) =
    grid.mapEachCharIndexed { row, col, tree -> grid.scenicScore(row,col,tree) }.max()

fun main() {
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)
    val input = readInput("Day08")
    println(part1(input))  // 1820
    println(part2(input))  // 385112
}
