package aoc04

import java.io.File

fun main() {
    val inputFile = File("inputs/aoc04/input.txt")
    val lines = inputFile.readLines()

    val width = lines[0].length
    val height = lines.size

    val count = xmasScan(lines, width, height)
    println(count)
}

fun xmasScan(lines: List<String>, width: Int, height: Int): Int {
    var counter = 0
    for (y in 0..<height) {
        for (x in 0..<width) {
            // horizontal
            val horizontal = lines[y].substring(startIndex = x)
            if (horizontal.startsWith("XMAS") || horizontal.startsWith("SAMX")) {
                counter++
            }

            // vertical
            if (lines.matches(listOf(x, x, x, x), y..y + 3, "XMAS") ||
                lines.matches(listOf(x, x, x, x), y..y + 3, "SAMX")
            ) {
                counter++
            }

            // diagonal (top-left to bottom-right)
            if (lines.matches(x..x + 3, y..y + 3, "XMAS") ||
                lines.matches(x..x + 3, y..y + 3, "SAMX")
            ) {
                counter++
            }

            // diagonal (top-right to bottom-left)
            if (lines.matches((x - 3..x).reversed(), y..y + 3, "XMAS") ||
                lines.matches((x - 3..x).reversed(), y..y + 3, "SAMX")
            ) {
                counter++
            }
        }
    }

    return counter
}