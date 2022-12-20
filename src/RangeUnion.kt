
class RangeUnion<T :Comparable<T>> {
    private class Range<T :Comparable<T>>(var from: T, var to: T)
    private val ranges = mutableListOf<Range<T>>()

    override fun toString() =
        ranges.joinToString(prefix = "{", postfix = "}") { "(${it.from}..${it.to})" }

    fun add(r: ClosedRange<T>) {
        if (ranges.isEmpty() || r.start > ranges.last().to)
            ranges.add(Range(r.start,r.endInclusive))
        else {
            var i = 0
            while(i < ranges.size) {
                val cr = ranges[i]
                when {
                    r.start < cr.from -> {
                        if (r.endInclusive < cr.from) {
                            ranges.add(i,Range(r.start,r.endInclusive))
                            break
                        }
                        if (r.endInclusive <= cr.to) {
                            cr.from = r.start
                            break
                        }
                        ranges.removeAt(i)
                    }
                    r.start == cr.from -> {
                        if (r.endInclusive <= cr.from) break
                        ranges.removeAt(i)
                    }
                    r.start <= cr.to -> {
                        if (r.endInclusive <= cr.from) break
                        while(i < ranges.lastIndex && r.endInclusive >= ranges[i+1].to)
                            ranges.removeAt(i+1)
                        if (i == ranges.lastIndex || r.endInclusive < ranges[i+1].from) {
                            cr.to = r.endInclusive
                            break
                        }
                        cr.to = ranges[i+1].to
                        ranges.removeAt(i+1)
                        break
                    }
                }
                ++i
            }
        }
    }
}

fun <T: Comparable<T>> rangeUnionOf(vararg rs: ClosedRange<T>): RangeUnion<T> {
    val ru = RangeUnion<T>()
    for (r in rs) ru.add(r)
    return ru
}

fun main() {
    println( RangeUnion<Int>() )
    println( rangeUnionOf(1..5) )
    println( rangeUnionOf(1..5, 7..9) )
    println( rangeUnionOf(1..5, -5..0) )
}