package aoc02

import java.io.File

@JvmInline
value class Report(val levels: IntArray) {
    override fun toString(): String = "Report {levels=${levels.contentToString()}}"
}

fun readReports(file: File): List<Report> =
    file.useLines { lines ->
        lines
            .map { line -> line.split(' ') }
            .map { lineItems -> IntArray(lineItems.size) { i -> lineItems[i].toInt() } }
            .map { levels -> Report(levels) }
            .toList()
    }