@file:Suppress("PackageDirectoryMismatch")
package day25
import readInput
import kotlin.math.abs

data class NumberSNAFU(val digits: String) {
    override fun toString() = digits
}

val DigitsSNAFU = mapOf('2' to 2, '1' to 1, '0' to 0, '-' to -1, '=' to -2)
val SymbolsSNAFU = DigitsSNAFU.keys.toList()

fun NumberSNAFU.toLong(): Long {
    var power = 1L
    var n = 0L
    digits.reversed().forEach { digit ->
        n += checkNotNull(DigitsSNAFU[digit]) * power
        power *= 5
    }
    return n
}

fun <T> List<T>.firstIndexed( predicate: (Int)->Boolean ): T {
    for (idx in indices)
        if (predicate(idx)) return get(idx)
    error("not found")
}

fun Long.toNumberSNAFU(): NumberSNAFU {
    if (this==0L) return NumberSNAFU("0")
    var power = 1L
    var max = power *2
    while (max  < abs(this)) { power *= 5; max += power*2 }
    var n = this
    val digits = StringBuilder()
    while(power > 0) {
        val dig = SymbolsSNAFU.firstIndexed { n > max-power*(it+1) }
        digits.append(dig)
        n -= DigitsSNAFU[dig]!! * power
        max -= power *2
        power /= 5
    }
    return NumberSNAFU( digits.toString() )
}

fun part1(lines: List<String>): String {
    val sum = lines.sumOf { NumberSNAFU(it).toLong() }
    return sum.toNumberSNAFU().digits
}

fun main() {
    val testInput = readInput("Day25_test")
    check(part1(testInput) == "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))  // 2=2-1-010==-0-1-=--2
}
