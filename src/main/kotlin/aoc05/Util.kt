package aoc05

val <T> List<T>.middleElement: T
    get() {
        check(size % 2 == 1) { "Cannot find middle element for list of even size" }
        val middleIndex = size / 2
        return this[middleIndex]
    }
