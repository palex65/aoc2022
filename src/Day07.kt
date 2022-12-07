
sealed class Entry(val name: String)
class File(name: String, val size: Int): Entry(name) {
    override fun toString() = "File($name,$size)"
}
class Dir(name: String, val parent: Dir?, var entries: List<Entry> = emptyList(), var size: Int=0): Entry(name) {
    override fun toString() = "Dir($name,$size {$entries} )"
    fun processSize() {
        size = entries.sumOf {
            when (it) {
                is Dir -> { it.processSize(); it.size}
                is File -> it.size
            }
        }
    }
    fun sumAtMost(max: Int): Int =
        entries.filterIsInstance<Dir>().sumOf {
            it.sumAtMost(max)
        } + if (size<=max) size else 0
    fun allSizes(): List<Int> {
        val dirs: List<Dir> = entries.filterIsInstance<Dir>()
        return if (dirs.isEmpty()) listOf(size) else
            dirs.map { it.allSizes() }.flatten() + size
    }
}

fun processCommands(cmds: List<Command>): Dir {
    val root = Dir("/",null)
    var dir = root
    for (cmd in cmds) {
        when(cmd) {
            is LS -> dir.entries = cmd.entries.map {
                val (a,b) = it.split(' ')
                if (a=="dir") Dir(b,dir) else File(b,a.toInt())
            }
            is CD -> dir =
                if (cmd.name=="..") dir.parent!!
                else dir.entries.first { cmd.name == it.name } as Dir
        }
    }
    return root
}

open class Command()
data class CD(val name: String) : Command()
data class LS(val entries: List<String>) : Command()

fun List<String>.toCommands(): List<Command> {
    val lst = mutableListOf<Command>()
    var idx = 0
    while (idx < size) {
        val cmd = this[idx++].substring(2).split(' ')
        when( cmd[0] ) {
            "ls" -> {
                var idxLast = idx
                while (idxLast < size && this[idxLast][0]!='$') ++idxLast
                lst.add( LS(subList(idx, idxLast) ) )
                idx = idxLast
            }
            "cd" -> lst.add( CD( cmd[1] ) )
        }
    }
    return lst
}

private fun part1(lines: List<String>): Int {
    val root = processCommands(lines.toCommands().drop(1))
    root.processSize()
    return root.sumAtMost(100000)
}

private fun part2(lines: List<String>): Int {
    val root = processCommands(lines.toCommands().drop(1))
    root.processSize()
    val toFree = 30000000 - (70000000-root.size)
    val allSizes = root.allSizes().sorted()
    return allSizes.first { it >= toFree }
}

fun main() {
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642) // 8381165

    val input = readInput("Day07")
    println(part1(input))  // 306611
    println(part2(input))  // 13210366
}
