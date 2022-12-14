
private data class Point(val x: Int, val y: Int)
private enum class Material(val symbol: Char){ ROCK('#'), SAND('o'), PATH('~') }

private fun Scan(lines: List<String>): MutableMap<Point,Material> {
    val res = mutableMapOf<Point,Material>()
    lines.forEach { path ->
        var start: Point? = null
        path.split(" -> ").map{ it.split(',') }.forEach { (a,b) ->
            val p = Point(a.toInt(),b.toInt())
            val s = start
            start = p
            if (s==null) res.put(p,Material.ROCK)
            else when {
                p.x==s.x && s.y<p.y -> for(y in s.y .. p.y ) res.put(Point(p.x,y),Material.ROCK)
                p.x==s.x && s.y>p.y -> for(y in p.y .. s.y ) res.put(Point(p.x,y),Material.ROCK)
                p.y==s.y && s.x<p.x -> for(x in s.x .. p.x ) res.put(Point(x,p.y),Material.ROCK)
                p.y==s.y && s.x>p.x -> for(x in p.x .. s.x ) res.put(Point(x,p.y),Material.ROCK)
            }
        }
    }
    return res
}

private fun printScan(s: Map<Point,Material>) {
    val orig = Point( s.keys.minOf{ it.x }, s.keys.minOf{ it.y })
    val max = Point( s.keys.maxOf{ it.x }, s.keys.maxOf{ it.y })
    println("Orig=$orig  Max=$max")
    for (y in orig.y .. max.y) {
        for(x in orig.x .. max.x) print( s[Point(x,y)]?.symbol ?: '.' )
        println()
    }
}

private val Point.drops get() = listOf( Point(x,y+1), Point(x-1,y+1), Point(x+1,y+1) )

private fun dropSand(from: Point, scan: MutableMap<Point,Material>): Point? {
    var p = from
    val maxY = scan.keys.maxOf { it.y }
    while(p.y <= maxY) {
        val drop = p.drops.firstOrNull {
            val material = scan[it]
            material==null || material==Material.PATH
        }
        if (drop==null) {
            scan[p] = Material.SAND
            return p
        } else {
            scan[p]=Material.PATH
            p = drop
        }
    }
    return null
}

private val sourceSand = Point(500,0)

private fun part1(lines: List<String>): Int {
    val scan = Scan(lines)
    do {
        val stop = dropSand(sourceSand,scan)
    } while( stop!=null )
    return scan.values.count { it == Material.SAND }
}

private fun part2(lines: List<String>): Int {
    val scan = Scan(lines)
    val floorY = scan.keys.maxOf { it.y } + 2
    do {
        val stop = dropSand(sourceSand,scan)
        if (stop==null) {
            val pathStopX = scan.filter{ it.value==Material.PATH }.maxBy{ it.key.y }.key.x
            scan[Point(pathStopX,floorY)] = Material.ROCK
        }
    } while( stop!=sourceSand )
    return scan.values.count { it == Material.SAND }
}

fun main() {
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))  // 757
    println(part2(input))  // 24943
}
