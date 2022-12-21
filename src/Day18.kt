@file:Suppress("PackageDirectoryMismatch")
package day18

import readInput

data class Cube(val x: Int, val y: Int, val z: Int)

fun Cube.connected(c: Cube) =
    x == c.x && y == c.y && (z == c.z-1 || z == c.z+1) ||
    y == c.y && z == c.z && (x == c.x-1 || x == c.x+1) ||
    z == c.z && x == c.x && (y == c.y-1 || y == c.y+1)

fun String.toCube(): Cube {
    val (x,y,z) = split(',').map{ it.toInt() }
    return Cube(x,y,z)
}

fun part1(lines: List<String>): Int {
    val cubes = lines.map { it.toCube() }
    var faces = cubes.size * 6
    cubes.forEachIndexed { i, cube ->
        for (j in i+1 .. cubes.lastIndex)
            if (cube.connected(cubes[j])) faces-=2
    }
    return faces
}

enum class Face(val dx: Int=0, val dy: Int=0, val dz: Int=0) {
    LEFT(dx=-1), RIGHT(dx=+1), UP(dy=-1), DOWN(dy=+1), FRONT(dz=+1), BACK(dz=-1)
}

fun Cube.connectedBy(f: Face) = Cube(x+f.dx, y+f.dy, z+f.dz)

fun Cube.around() = Face.values().map{ connectedBy(it) }

class Cubes(lines: List<String>) {
    val list = lines.map { it.toCube() }
    val all = list.toSet()
    val minX = list.minOf { it.x }
    val maxX = list.maxOf { it.x }
    val minY = list.minOf { it.y }
    val maxY = list.maxOf { it.y }
    val minZ = list.minOf { it.z }
    val maxZ = list.maxOf { it.z }

    fun Cube.isValid() = x in minX-1 .. maxX+1 && y in minY-1 .. maxY+1 && z in minZ-1 .. maxZ+1
}

fun part2(lines: List<String>): Int = with( Cubes(lines) ) {
    val outside = mutableSetOf<Cube>()
    val open = mutableListOf( Cube(minX-1,minY-1,minZ-1) ) // Assume is outside
    var faces = 0
    while (open.isNotEmpty()) {
        val c = open.removeFirst()
        outside.add(c)
        c.around().forEach { a ->
            if ( a in all ) faces++
            else
                if (a.isValid() && a !in outside && a !in open)
                    open.add(a)
        }
    }
    faces
}

fun main() {
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))  // 3526
    println(part2(input))  // 2090
}
