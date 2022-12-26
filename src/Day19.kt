@file:Suppress("PackageDirectoryMismatch")
@file:OptIn(ExperimentalTime::class)

package day19
import readInput
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

data class Blueprint(
    val id: Int,
    val ore: Int,                   // in Ore
    val clay: Int,                  // in Ore
    val obsidian: Pair<Int,Int>,    // in Ore,Clay
    val geode: Pair<Int,Int>,       // in Ore,Obsidian
)

const val INT = """(\d+)"""
val blueprintPattern = Regex(
    """Blueprint $INT: 
        |Each ore robot costs $INT ore. 
        |Each clay robot costs $INT ore. 
        |Each obsidian robot costs $INT ore and $INT clay. 
        |Each geode robot costs $INT ore and $INT obsidian.""".trimMargin().replace("\n","")
)

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]

fun Blueprint(line: String): Blueprint {
    val (id, ore, clay, obs1, obs2, geo1, geo2) = (blueprintPattern.find(line) ?: error("Parsing $line"))
        .destructured.toList().map { it.toInt() }
    return Blueprint(id,ore,clay,Pair(obs1,obs2),Pair(geo1,geo2))
}

data class RobotState(val n: Int, val collect: Int) :Comparable<RobotState> {
    override fun toString() = "($n,$collect)"
    override fun compareTo(other: RobotState) = when {
        n == other.n && collect == other.collect -> 0
        n >= other.n && collect >= other.collect -> 1
        else -> -1
    }
}

fun RobotState.next(nDif: Int= 0, collectDif: Int= 0) =
    RobotState(n + nDif, collect + n + collectDif)

data class State(
    val time: Int,
    val ore: RobotState,
    val clay: RobotState = RobotState(0,0),
    val obsidian: RobotState = RobotState(0,0),
    val geode: RobotState = RobotState(0,0),
): Comparable<State> {
    override fun compareTo(other: State) = other.h - h
    val h = geode.n + obsidian.n + clay.n + ore.n
    override fun toString() = "Sate(h=$h, time=$time, $ore, $clay, $obsidian, $geode"
}


infix fun State.isBestThan(s: State) =
    time <= s.time && geode >= s.geode && obsidian >= s.obsidian && clay >= s.clay && ore >= s.ore

fun State.next(
    ore: RobotState= this.ore.next(),
    clay: RobotState= this.clay.next(),
    obsidian: RobotState= this.obsidian.next(),
    geode: RobotState= this.geode.next()
) = State(time+1,ore,clay,obsidian,geode)

fun Blueprint.after(s: State): List<State> = buildList {
    if (s.ore.collect >= geode.first && s.obsidian.collect >= geode.second)
        add( s.next(
            ore= s.ore.next(collectDif = -geode.first),
            obsidian = s.obsidian.next(collectDif = -geode.second),
            geode = s.geode.next(+1)
        ) )
    else if (s.ore.collect >= obsidian.first && s.clay.collect >= obsidian.second)
        add( s.next(
            ore= s.ore.next(collectDif = -obsidian.first),
            clay = s.clay.next(collectDif = -obsidian.second),
            obsidian = s.obsidian.next(+1)
        ) )
    else {
        if (s.ore.collect >= clay)
            add(s.next(ore = s.ore.next(collectDif = -clay), clay = s.clay.next(+1)))
        if (s.ore.collect >= ore)
            add(s.next(ore = s.ore.next(+1, -ore)))
    }
    add( s.next() )
}

fun Blueprint.simulateToMax(limitTime: Int): Int {
    val best = mutableListOf<State>()
    val visited = mutableSetOf<State>()
    val open = mutableListOf( State(time=0, ore=RobotState(1,0)) )
    var maxGeodes = 0

    while (open.isNotEmpty()) {
        val state = open.removeFirst()
        maxGeodes = maxOf(maxGeodes, state.geode.collect)
        if (state.time == limitTime)
            continue
        after(state)
            .filter { it !in visited }
            .forEach { s ->
                visited.add( s )
                if ( best.none { it.isBestThan(s) } ) {
                    best.add(s)
                    if (best.size > 500) {
                        val worst = best.minOf { it.h }
                        best.removeAll { it.h == worst }
                    }
                    open.add(s)
                }
            }
    }
    return maxGeodes
}

fun Blueprint.timedSimulation(limitTime: Int): Int {
    val (geodes,tm) = measureTimedValue { this.simulateToMax(limitTime) }
    println("Blueprint $id: geodes=$geodes in $tm")
    return geodes
}

fun part1(lines: List<String>): Int {
    val blueprints = lines.map { Blueprint(it) }
    return blueprints
        .map { it.timedSimulation(24) }
        .zip(blueprints)
        .sumOf { (geodes, blueprint) -> blueprint.id * geodes }
}

fun part2(lines: List<String>): Int {
    val blueprints = lines.take(3).map { Blueprint(it) }
    return blueprints
        .map { it.timedSimulation(32) }
        .reduce{ product, geodes -> product * geodes }
}

fun main() {
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)
    check(part2(testInput) == 54*62)

    val input = readInput("Day19")
    println(part1(input))  // 1766
    println(part2(input))  // 30780
}
