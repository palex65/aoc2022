import kotlin.math.min

private fun compareSeq(a: List<String>, b: List<String>): Int {
    for(i in 0 until min(a.size,b.size)) {
        val res = compare(a[i], b[i])
        if (res!=0) return res
    }
    return a.size - b.size
}

private fun String.indexOfClosedPair(from: Int): Int {
    var level = 1
    for( i in from until length )
        when(this[i]) {
            '[' -> ++level
            ']' -> if (--level==0) return i
        }
    return -1
}

private fun String.splitSeq(): List<String>  {
    val res = mutableListOf<String>()
    var lastIdx = 0
    var idx = 0
    while(idx < length) {
        when(this[idx]) {
            ',' -> {
                res.add( substring(lastIdx,idx) )
                lastIdx=++idx
            }
            '[' -> idx = indexOfClosedPair(idx+1)+1
            else -> ++idx
        }
    }
    if (idx>lastIdx) res.add( substring(lastIdx,idx))
    return res
}

private fun compare(a: String, b: String): Int {
    val fa = a[0]
    val fb = b[0]
    fun String.toSeq() = substring(1,lastIndex).splitSeq()
    return when {
        fa.isDigit() && fb.isDigit() -> a.toInt() - b.toInt()
        fa=='[' && fb=='[' -> compareSeq( a.toSeq(), b.toSeq() )
        fa=='[' -> compareSeq( a.toSeq(), listOf(b) )
        fb=='[' -> compareSeq( listOf(a), b.toSeq() )
        else -> error("Invalid start of package $fa or $fb")
    }
}

private fun part1(lines: List<String>): Int =
    lines
        .splitBy { it.isEmpty() }
        .mapIndexed { idx, pair -> if (compare(pair[0], pair[1]) < 0) idx+1 else 0  }
        .sum()

private fun part2(lines: List<String>): Int {
    val packets = lines.filter{ it.isNotEmpty() } + "[[2]]" + "[[6]]"
    val sorted = packets.sortedWith(::compare)
    return (sorted.indexOf("[[2]]")+1) * (sorted.indexOf("[[6]]")+1)
}

fun main() {
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))  // 5390
    println(part2(input))  // 19261
}
