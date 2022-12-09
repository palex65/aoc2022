private typealias Device = Map<String,Int>

private fun part1(dev: Device): Int =
    dev.values.sumOf { if (it<=100000) it else 0 }

private fun part2(dev: Device): Int {
    val toFree = 30000000 - (70000000 - (dev[""]?:0))
    return dev.values.filter { it>=toFree }.min()
}

private fun Device(lines: List<String>): Map<String,Int> =  buildMap {
    var wd = ""
    val idxCmds = lines.mapIndexedNotNull { idx, line -> if (line[0]=='$' && idx>0) idx else null }
    idxCmds.forEachIndexed { idx, idxCmd ->
        val cmdLine = lines[idxCmd].substringAfter("$ ")
        when (cmdLine.substringBefore(' ')) {
            "ls" -> {
                val size = lines
                    .subList(idxCmd + 1, if (idx < idxCmds.lastIndex) idxCmds[idx + 1] else lines.size)
                    .filter { !it.startsWith("dir") }.sumOf { it.substringBefore(' ').toInt() }
                var dir = wd
                while(true) {
                    put(dir, (get(dir) ?: 0) + size)
                    if (dir=="" || size==0) break
                    dir = dir.substringBeforeLast('/')
                }
            }
            "cd" -> wd = when (val name = cmdLine.substringAfter(' ')) {
                ".." -> wd.substringBeforeLast('/')
                else -> "$wd/$name"
            }
        }
    }
}

fun main() {
    val dev_test = Device(readInput("Day07_test"))
    check(part1(dev_test) == 95437)
    check(part2(dev_test) == 24933642)

    val dev = Device(readInput("Day07"))
    println(part1(dev))  // 1306611
    println(part2(dev))  // 13210366
}
