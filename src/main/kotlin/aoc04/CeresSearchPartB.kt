package aoc04

import java.io.File

fun main() {
    val inputFile = File("inputs/aoc04/input.txt")
    val lines = inputFile.readLines()

//    val lines = """
//        MMMSXXMASM
//        MSAMXMSMSA
//        AMXSXMAAMM
//        MSAMASMSMX
//        XMASAMXAMM
//        XXAMMXXAMA
//        SMSMSASXSS
//        SAXAMASAAA
//        MAMMMXMMMM
//        MXMXAXMASX
//    """.trimIndent().lines()

    val width = lines[0].length
    val height = lines.size

    val count = crossXmasScan(lines, width, height)
    println(count)
}

fun crossXmasScan(lines: List<String>, width: Int, height: Int): Int {
    var counter = 0
    for (y in 0..<height) {
        for (x in 0..<width) {
            // MAS - MAS
            if (lines.matches(x..x + 2, y..y + 2, "MAS") &&
                lines.matches(x..x + 2, (y..y + 2).reversed(), "MAS")
            ) {
                counter++
            }

            // MAS - SAM
            if (lines.matches(x..x + 2, y..y + 2, "MAS") &&
                lines.matches(x..x + 2, (y..y + 2).reversed(), "SAM")
            ) {
                counter++
            }

            // SAM - MAS
            if (lines.matches(x..x + 2, y..y + 2, "SAM") &&
                lines.matches(x..x + 2, (y..y + 2).reversed(), "MAS")
            ) {
                counter++
            }

            // SAM - SAM
            if (lines.matches(x..x + 2, y..y + 2, "SAM") &&
                lines.matches(x..x + 2, (y..y + 2).reversed(), "SAM")
            ) {
                counter++
            }
        }
    }

    return counter
}