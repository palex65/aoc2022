@file:Suppress("PackageDirectoryMismatch")
package day20
import readInput
import kotlin.math.*

fun mix(numbers: List<Long>, times: Int = 1): List<Long> {
    val ns = numbers.withIndex().toMutableList()
    repeat(times) {
        for (idx in 0..numbers.lastIndex) {
            var i = ns.indexOfFirst { it.index == idx }
            val n = ns[i]
            val pos = (n.value % (ns.size - 1)).toInt()
            val dif = pos.sign
            repeat(abs(pos)) { i = ((i + dif).mod(ns.size)).also { ns[i] = ns[it] } }
            ns[i] = n
        }
    }
    return ns.map { it.value }
}

fun List<Long>.sum3(): Long {
    val idx0 = indexOf(0)
    return (idx0..idx0+3000 step 1000).sumOf { get(it % size) }
}

fun part1(lines: List<String>): Long =
    mix( lines.map { it.toLong() } ).sum3()

fun part2(lines: List<String>): Long =
    mix( lines.map { (it.toLong() * 811589153) }, 10 ).sum3()

fun main() {
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))  // 5498
    println(part2(input))  // 3390007892081
}
