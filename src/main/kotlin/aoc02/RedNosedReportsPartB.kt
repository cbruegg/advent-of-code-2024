package aoc02

import java.io.File
import kotlin.math.abs

fun main() {
    val inputFile = File("inputs/aoc02/reports.txt")
    val reports = readReports(inputFile)

    val numberOfLenientlySafeReports = reports.count { it.isLenientlySafeNaive() }
    println(numberOfLenientlySafeReports)
}

// For report with N values and Report.isSafe taking O(N):
// O(N * N)
// 1st N: outer loop
// 2nd N: withoutIndex(i) + isSafe()
fun Report.isLenientlySafeNaive(): Boolean {
    if (isSafe()) return true

    for (i in levels.indices) {
        if (Report(levels = levels.withoutIndex(i)).isSafe()) {
            return true
        }
    }

    return false
}

// For report with N values and Report.isSafe taking O(N):
// Funny enough, this is still O(N * N) in worst case.
// Even in best case (i.e. we're a good report already), this isn't better than isLenientlySafeNaive - both are O(N).
// Only when the bad element is far at the end of the report, isLenientlySafe() becomes more efficient
// as it will run isSafe() on just three candidate report variants around the bad element.
fun Report.isLenientlySafe(): Boolean {
    if (levels.size < 2) {
        return true
    }

    if (levels[0] == levels[1]) {
        // Initial pair already violates increasing/decreasing requirement.
        // Let's check if we can fix the report by dropping index 0 or 1:
        return Report(levels = levels.withoutIndex(0)).isSafe() || Report(levels = levels.withoutIndex(1)).isSafe()
    }

    // Initial guess
    val looksIncreasing = levels[0] < levels[1]

    for (i in 1..<levels.lastIndex) {
        val cur = levels[i]
        val next = levels[i + 1]

        if (looksIncreasing && cur > next || !looksIncreasing && cur < next) {
            // Initial guess violated. Let's check if we can fix
            // the report by dropping previous, current or next:
            return (i - 1..i + 1).any { Report(levels = levels.withoutIndex(it)).isSafe() }
        }

        val diff = abs(cur - next)
        if (diff !in 1..3) {
            // Diff not in safe range. Let's check if we can fix
            // the report by dropping previous, current or next:
            return (i - 1..i + 1).any { Report(levels = levels.withoutIndex(it)).isSafe() }
        }
    }

    return true
}

fun IntArray.withoutIndex(deletedIndex: Int) =
    IntArray(size - 1) { i ->
        if (i < deletedIndex) this[i] else this[i + 1]
    }