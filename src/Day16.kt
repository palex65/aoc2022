@file:Suppress("PackageDirectoryMismatch")
package day16

import readInput
import java.util.PriorityQueue
import kotlin.math.*

const val INT = """(\d+)"""
const val NAME = """([A-Z]+)"""
const val REST = """([A-Z, ]+)"""
val valvePattern = Regex("""Valve $NAME has flow rate=$INT; tunnels? leads? to valves? $REST$""")

data class Valve(val name: String, val rate: Int, val to: List<String>) : Comparable<Valve>{
    override fun compareTo(other: Valve) = compareValuesBy(this,other) { it.name }
    var tunnels: List<Valve> = emptyList()
        private set
    val pathsToRated by lazy { getPathsToValidValves(this) }

    fun buildTunnels(valves: List<Valve>) { tunnels = to.map { name -> valves.first { it.name == name } }}

    private fun getPathsToValidValves(from: Valve, dist: Int = 0, visited: Set<Valve> = setOf(from)): Map<Valve,Int> =
        buildMap {
            for (valve in from.tunnels.filter { it !in visited }) {
                if (valve.rate != 0) set(valve, dist + 1)
                for ((v, d) in getPathsToValidValves(valve, dist + 1, visited + valve)) {
                    val dd = get(v)
                    set(v, if (dd == null) d else min(d, dd))
                }
            }
        }
}

fun Valve(line: String): Valve {
    val (n, r, t) = (valvePattern.find(line) ?: error("invalid valve \"$line\"")).destructured
    return Valve(n, r.toInt(), t.split(", "))
}

data class State(
    val time: Int,
    val curr: Valve,
    val open: List<Valve> = emptyList(),
    val pressure: Int = 0,
    val timeElephant: Int = 0,
    val currElephant: Valve = curr,
) : Comparable<State> {
    override fun compareTo(other: State) = compareValuesBy(this,other){ -it.pressure }
}

fun part1(lines: List<String>): Int {
    val valves = lines.map { Valve(it) }
    valves.forEach { it.buildTunnels(valves) }

    val init = valves.first { it.name=="AA" }
    val queue = PriorityQueue<State>().also { it.add(State(30, init)) }
    var best = 0
    val visited: MutableMap<List<Valve>, Int> = mutableMapOf()
    while (queue.isNotEmpty()) {
        val state = queue.remove()
        val (time, curr, open, pressure) = state
        best = max(best, pressure)
        val vis = visited[open]
        if (vis!=null && vis >= pressure) continue
        visited[open] = pressure
        for ((next, dist) in curr.pathsToRated) {
            val newTime = time-dist-1
            if (newTime >= 0 && next !in open)
                queue.add(State(newTime, next, (open + next).sorted(), pressure + next.rate * newTime))
        }
    }
    println("Visited = ${visited.size}")
    return best
}

fun part2(lines: List<String>): Int {
    val valves = lines.map { Valve(it) }
    valves.forEach { it.buildTunnels(valves) }

    val init = valves.first { it.name=="AA" }
    val queue = PriorityQueue<State>().also { it.add(State(26, init, emptyList(), 0, 26, init)) }
    var best = 0
    val visited: MutableMap<List<Valve>, Int> = mutableMapOf()
    while (queue.isNotEmpty()) {
        val state = queue.remove()
        var (time, curr, open, pressure, timeE, currE) = state
        best = max(best, pressure)
        val vis = visited[open]
        if (vis!=null && vis >= pressure) continue
        visited[open] = pressure
        if (time < timeE) {
            time = timeE.also { timeE = time }
            curr = currE.also { currE = curr }
        }
        for ((next, dist) in curr.pathsToRated) {
            val newTime = time-dist-1
            if (newTime >= 0 && next !in open)
                queue.add(State(newTime, next, (open + next).sorted(), pressure + next.rate * newTime, timeE, currE))
        }
    }
    println("Visited = ${visited.size}")
    return best
}

fun main() {
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))  // 1789
    println(part2(input))  // 2495
}
