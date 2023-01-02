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
    override fun hashCode() = name.hashCode()
    override fun compareTo(other: Valve) = compareValuesBy(this,other) { it.name }
    private var tunnels: List<Valve> = emptyList()
    var tunnelsToRated: Map<Valve,Int> = emptyMap()

    fun buildTunnels(valves: Map<String,Valve>) { tunnels = to.map { name -> checkNotNull(valves[name]) } }
    fun buildRatedTunnels() {
        fun build(from: Valve, dist: Int, visited: Set<Valve>, to: MutableMap<Valve,Int>): Map<Valve,Int> {
            val rated = from.tunnels
                .filter { it !in visited }
            rated.filter { it.rate > 0 }
                .forEach { to[it] = minOf(dist+1, to[it] ?: Int.MAX_VALUE) }
            rated.forEach { build(it, dist+1, visited+from, to) }
            return to
        }
        tunnelsToRated = build(this, 0, emptySet(), mutableMapOf())
    }
}

fun Valve(line: String): Valve {
    val (n, r, t) = (valvePattern.find(line) ?: error("invalid valve \"$line\"")).destructured
    return Valve(n, r.toInt(), t.split(", "))
}

typealias Valves = Map<String,Valve>

fun Valves(lines: List<String>): Valves {
    val valves = lines.map { Valve(it) }.associateBy { it.name }
    valves.values.forEach { it.buildTunnels(valves) }
    valves.values.forEach { it.buildRatedTunnels() }
    return valves
}

val Valves.rated get() = values.filter { it.rate > 0 }.sortedBy { -it.rate }

data class State(
    val time: Int,  // Remaining time
    val curr: Valve,
    val opened: List<Valve> = emptyList(),
    val closed: List<Valve>,
    val pressure: Int = 0,
    val timeElephant: Int = 0,          // Part2
    val currElephant: Valve = curr,     // Part2
) : Comparable<State> {
    private val totalPressure: Int = pressure + estimateClosed()
    override fun compareTo(other: State) = compareValuesBy(other,this){ it.totalPressure }
    private fun estimateClosed(): Int {
        var valve = curr
        var tm = time
        var press = 0
        closed.forEach { v ->
            tm -= valve.tunnelsToRated[v]!! + 1
            press += v.rate * tm
            valve = v
        }
        return press
    }
    override fun toString() = "State($time,${curr.name},opened=${opened.map { it.name }},closed=${closed.map { it.name }},$pressure,$totalPressure)"
}

fun part1(valves: Valves): Int {
    val open = PriorityQueue<State>()
    open.add(State(30, valves["AA"]!!, closed = valves.rated ))
    var best = 0
    val visited: MutableMap<List<Valve>, Int> = mutableMapOf()
    while (open.isNotEmpty()) {
        val state = open.remove()
        best = max(best, state.pressure)
        val vis = visited[state.opened]
        if (vis!=null && vis > state.pressure) continue
        visited[state.opened] = state.pressure
        for ((next, dist) in state.curr.tunnelsToRated) {
            val newTime = state.time-dist-1
            if (newTime >= 0 && next !in state.opened)
                open.add(State(newTime, next, (state.opened + next).sorted(), state.closed-next, state.pressure + next.rate * newTime))
        }
    }
    return best
}

fun part2(valves: Valves): Int {
    val init = valves["AA"]!!
    val open = PriorityQueue<State>()
    open.add(State(26, init, emptyList(), valves.rated, 0, 26, init))
    var best = 0
    val visited: MutableMap<List<Valve>, Int> = mutableMapOf()
    while (open.isNotEmpty()) {
        val state = open.remove()
        best = max(best, state.pressure)
        val vis = visited[state.opened]
        if (vis!=null && vis > state.pressure) continue
        visited[state.opened] = state.pressure
        var time = state.time
        var curr = state.curr
        var timeE = state.timeElephant
        var currE = state.currElephant
        if (time < timeE) {
            time = timeE.also { timeE = time }
            curr = currE.also { currE = curr }
        }
        for ((next, dist) in curr.tunnelsToRated) {
            val newTime = time-dist-1
            if (newTime >= 0 && next !in state.opened)
                open.add(State(newTime, next, (state.opened + next).sorted(), state.closed-next, state.pressure + next.rate * newTime, timeE, currE))
        }
    }
    return best
}

fun main() {
    val testValves = Valves(readInput("Day16_test"))
    check(part1(testValves) == 1651)
    check(part2(testValves) == 1707)

    val valves = Valves(readInput("Day16"))
    println(part1(valves))  // 1789
    println(part2(valves))  // 2496
}
