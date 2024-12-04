package aoc04

fun List<String>.matches(x: Iterable<Int>, y: Iterable<Int>, str: String): Boolean {
    for ((i, xyPair) in x.zip(y).withIndex()) {
        val (x1, y1) = xyPair
        if (y1 !in this.indices || x1 !in this[y1].indices || this[y1][x1] != str[i]) {
            return false
        }
    }

    return true
}