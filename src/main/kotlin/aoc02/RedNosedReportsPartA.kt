package aoc02

import java.io.File
import kotlin.math.abs

fun main() {
    val inputFile = File("inputs/aoc02/reports.txt")
    val reports = readReports(inputFile)

    val numberOfSafeReports = reports.count { it.isSafe() }
    println(numberOfSafeReports)
}

fun Report.isSafe(): Boolean {
    if (levels.size < 2) {
        return true
    }

    val isIncreasing = levels[0] < levels[1]
    for (i in 0..<levels.lastIndex) {
        val cur = levels[i]
        val next = levels[i + 1]

        if (isIncreasing && cur > next) {
            return false
        }
        if (!isIncreasing && cur < next) {
            return false
        }

        val diff = abs(cur - next)
        if (diff !in 1..3) {
            return false
        }
    }

    return true
}