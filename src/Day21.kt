@file:Suppress("PackageDirectoryMismatch")
package day21
import readInput

abstract class Monkey(val name: String) {
    abstract fun getNumber(monkeys: Map<String,Monkey>): Long?
    open fun getForResult(monkeys: Map<String, Monkey>, res: Long): Long = error("getForResult in $name")
}

class MonkeyNumber(name: String, private val number: Int): Monkey(name) {
    override fun getNumber(monkeys: Map<String,Monkey>) =  number.toLong()
}

class Human: Monkey("humn") {
    override fun getNumber(monkeys: Map<String,Monkey>) =  null
    override fun getForResult(monkeys: Map<String, Monkey>, res: Long) = res
}

class MonkeyExpr(name: String, val left: String, val op: Char, val right: String): Monkey(name) {
    override fun getNumber(monkeys: Map<String,Monkey>): Long? {
        val nLeft = monkeys[left]?.getNumber(monkeys)
        val nRight = monkeys[right]?.getNumber(monkeys)
        return if (nLeft==null || nRight==null) null
        else when (op) {
            '+' -> nLeft + nRight
            '-' -> nLeft - nRight
            '/' -> nLeft / nRight
            '*' -> nLeft * nRight
            else -> error("Invalid operator $op")
        }
    }
    override fun getForResult(monkeys: Map<String, Monkey>, res: Long): Long {
        val l = monkeys[left]
        val r = monkeys[right]
        check(l!=null && r!=null)
        val nLeft = l.getNumber(monkeys)
        return if (nLeft==null) {
            val nRight = r.getNumber(monkeys) ?: error("Right null")
            l.getForResult(monkeys, when(op) {
                '+' -> res - nRight
                '-' -> res + nRight
                '*' -> res / nRight
                else -> res * nRight
            } )
        } else {
            r.getForResult(monkeys, when(op) {
                '+' -> res - nLeft
                '-' -> nLeft - res
                '*' -> res / nLeft
                else -> nLeft / res
            } )
        }
    }
}

class Root(val left: String, val right: String): Monkey("root") {
    override fun getNumber(monkeys: Map<String, Monkey>): Long? {
        val l = monkeys[left]
        val r = monkeys[right]
        check(l!=null && r!=null)
        val nLeft = l.getNumber(monkeys)
        return if (nLeft==null)
            l.getForResult(monkeys,r.getNumber(monkeys)!!)
        else
            r.getForResult(monkeys,l.getNumber(monkeys)!!)
    }
}

fun String.toMonkey(part2: Boolean): Monkey {
    val (name,rest) = split(": ")
    val value = rest.split(' ')
    if (part2)
        when (name) {
            "root" -> return Root(value[0],value[2])
            "humn" -> return Human()
        }
    return if (value.size==1) MonkeyNumber(name, rest.toInt())
    else MonkeyExpr(name,value[0],value[1].first(),value[2])
}

fun partN(lines: List<String>, part2: Boolean = false): Long {
    val monkeys = lines.map { it.toMonkey(part2) }.associateBy { it.name }
    val root = monkeys["root"] ?: return 0
    return root.getNumber(monkeys) ?: 0
}

fun part1(lines: List<String>) = partN(lines)

fun part2(lines: List<String>) = partN(lines, part2 = true)

fun main() {
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    println(part1(input))  // 268597611536314
    println(part2(input))  // 3451534022348
}
