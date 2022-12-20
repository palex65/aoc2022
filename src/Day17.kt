@file:Suppress("PackageDirectoryMismatch")
package day17

import readInput

fun String.toLeft(): String {
    val s = StringBuilder(this)
    s[ indexOf('@')-1 ] = '@'
    s[ lastIndexOf('@') ] = '.'
    return s.toString()
}

fun String.toRight(): String {
    val s = StringBuilder(this)
    s[ indexOf('@') ] = '.'
    s[ lastIndexOf('@')+1 ] = '@'
    return s.toString()
}

infix fun String.or(s: String): String {
    val res = StringBuilder(s)
    forEachIndexed { i, c ->
        if (c=='#') res[i]='#'
    }
    return res.toString()
}

class Chamber(val pushes: String) {
    val tall = 0L
    val tower = mutableListOf<String>()
    var pushIdx = 0
}

fun partN(line: String, n: Long): Long {
    val chamber = mutableListOf<String>() // MutableList(3){ "......." }
    var dirIdx = 0
    var offset = 0L
    var period = -1
    var start = 0

    fun printChamber(label: String) {
        println("----- $label -----")
        var i = chamber.lastIndex
        if (offset != 0L) {
            while (i>=start+period*3) {
                println("|${chamber[i]}| ${offset+i}")
                --i
            }
        }
        while(i>=0) {
            println("|${chamber[i]}| $i")
            --i
        }
        println("+-------+")
    }

    fun canShift(dir: Char): Int {
        var i = chamber.lastIndex
        lateinit var l: String
        while ('@' !in chamber[i]) --i
        val res = i
        while (i>=0 && '@' in chamber[i--].also { l=it }) {
            if (dir=='>') {
                val j = l.lastIndexOf('@')
                if (j==l.lastIndex || l[j+1]!='.') return -1
            } else {
                val j = l.indexOf('@')
                if (j==0 || l[j-1]!='.') return -1
            }
        }
        return res
    }

    fun toLeftOrRight() {
        val dir = line[dirIdx]
        dirIdx = ++dirIdx % line.length
        var i = canShift(dir)
        lateinit var l: String
        while (i>=0 && '@' in chamber[i].also { l=it })
            chamber[i--] = if (dir=='>') l.toRight() else l.toLeft()
        //printChamber("to $dir")
    }

    fun toDown(): Boolean {
        var i = chamber.lastIndex
        while ('@' !in chamber[i]) --i
        while ( i >= 0 && '@' in chamber[i] ) {
            if (i==0) return false
            chamber[i].forEachIndexed { j, c ->
                if (c=='@' && chamber[i-1][j]=='#') return false
            }
            --i
        }
        var down = StringBuilder(chamber[i])
        var any = true
        while (i < chamber.lastIndex && any) {
            val up = StringBuilder(chamber[i+1])
            any = false
            up.forEachIndexed { j, c ->
                if (c == '@') {
                    down[j] = '@'; up[j] = '.'; any = true
                }
            }
            chamber[i++] = down.toString()
            down = up
        }
        if (down.all { it=='.' }) chamber.removeLast()
        else chamber[i] = down.toString()
        //printChamber("toDow")
        return true
    }

    fun fix() {
        var i = chamber.lastIndex
        lateinit var l: String
        while ( '@' !in chamber[i] ) --i
        while ( i >= 0 && '@' in chamber[i].also { l=it } ) {
            chamber[i--] = l.replace('@', '#')
        }
    }

    val detectSize = 100
    fun detectPeriod() {
        if (period != -1 || chamber.size < detectSize) return
        val top = chamber.lastIndex
        var i=top - 10
        while(i > top/2) {
            i--
            if (chamber[i] == chamber[top]) {
                var j = 1
                while(i-j >=0 && chamber[i-j] == chamber[top-j]) {
                    ++j
                    if (i == top-j) {
                        period = j
                        start = chamber.lastIndex-2*j+1
                        //println("start = $start, period =$j")
                        //printChamber("Period")
                        return
                    }
                }
            }
        }
    }

    var pieceType = 0
    var repeat = n
    var countPieces = -1

    while (repeat > 0L) {
        chamber.addAll( List(3){ "......." } )
        chamber.addAll( when (pieceType) {
            0 -> listOf("..@@@@.")  // -
            1 -> listOf("...@...","..@@@..","...@...") // +
            2 -> listOf("..@@@..","....@..","....@..") // J
            3 -> listOf("..@....","..@....","..@....","..@....") // I
            else /*4*/ -> listOf("..@@...","..@@...") // O
        } )
        //printChamber("Start $it")
        do
            toLeftOrRight()
        while( toDown() )
        fix()
        pieceType = (pieceType+1) % 5
        repeat = repeat.dec()
        detectPeriod()
        if (period>0 && offset==0L) {
            if (countPieces==-1)
                countPieces=0
            else
                ++countPieces
            if (chamber.lastIndex >= start+3*period) {
                --countPieces
                val m = repeat / countPieces
                offset = m * period
                repeat -= m * countPieces
            }
        }
    }
    val res = chamber.size + offset
    //printChamber("End  res=$res")
    return res
}

fun part1(line: String) = partN(line, 2022L )
fun part2(line: String) = partN(line, 1000000000000L )

fun main() {
    val testInput = readInput("Day17_test")
    check(part1(testInput.first()) == 3068L)
    check(part2(testInput.first()) == 1514285714288L)

    val input = readInput("Day17")
    println(part1(input.first()))  // 3141
    println(part2(input.first()))  // 1561739130391
}
