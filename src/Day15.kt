@file:Suppress("PackageDirectoryMismatch")
package day15

import readInput
import kotlin.math.*

data class Pos(val x:Int, val y: Int)
fun Pos.distance(p: Pos) = abs(p.x-x)+abs(p.y-y)

data class Sensor(val pos: Pos, val beacon: Pos){
    val range = pos.distance(beacon)
    val minX get() = pos.x - range
    val maxX get() = pos.x + range
    fun contains(x: Int, y: Int) = abs(pos.x-x)+abs(pos.y-y) <= range
}

const val INT = """(\-?\d+)"""
val sensorPattern = Regex("""Sensor at x=$INT, y=$INT: closest beacon is at x=$INT, y=$INT""")

fun Sensor(line: String): Sensor {
    val (sx, sy, bx, by) = (sensorPattern.find(line) ?: error("invalid sensor $line"))
        .destructured.toList()
        .map { it.toInt() }
    return Sensor(Pos(sx,sy), Pos(bx,by))
}

fun part1(lines: List<String>, y: Int): Int {
    val sensors = lines.map { Sensor(it) }
    val beacons = sensors.map { it.beacon }.toSet()
    val res = (sensors.minOf { it.minX }..sensors.maxOf { it.maxX }).count { x ->
        val p = Pos(x,y)
        p !in beacons && sensors.any { it.pos.distance(p) <= it.range }
    }
    return res
}

class RangesUnion {
    private data class Mark(val value: Int, var delta: Int)
    private val marks = mutableListOf<Mark>()
    fun addRange(from: Int, exclusiveTo: Int) {
        if (from >= exclusiveTo) return
        var i = 0
        while (i < marks.size && from > marks[i].value) ++i
        if (i<marks.size && marks[i].value == from) marks[i].delta +=1
        else marks.add(i,Mark(from,1))
        ++i
        while (i < marks.size && exclusiveTo > marks[i].value) ++i
        if (i<marks.size && marks[i].value == exclusiveTo) marks[i].delta -=1
        else marks.add(i,Mark(exclusiveTo,-1))
    }
    fun getFirstNotContainedIn(range: IntRange): Int? {
        if (marks.isEmpty() || marks.first().value > range.first)
            return range.first
        check(marks.first().delta>0)
        var inRange = 0
        for(m in marks) {
            inRange += m.delta
            if (m.value > range.last) return null
            if (inRange==0 && m.value >= range.first)
                return m.value
        }
        return null
    }
}

fun part2(lines: List<String>, max: Int): Long {
    val sensors = lines.map { Sensor(it) }
    for (y in 0..max) {
        val excludes = RangesUnion()
        sensors.forEach {
            val d = abs(y-it.pos.y)
            if ( d <= it.range) {
                val dx = it.range - d
                val x = it.pos.x
                excludes.addRange(x-dx, x+dx+1)
            }
        }
        val x = excludes.getFirstNotContainedIn(0..max)
        if (x != null) return x * 4000000L + y
    }
    return -1
}

fun main() {
    val testInput = readInput("Day15_test")
    check(part1(testInput,10) == 26)
    check(part2(testInput,20) == 56000011L)

    val input = readInput("Day15")
    println(part1(input,2000000))  // 5403290
    println(part2(input,4000000))  // 10291582906626
}
