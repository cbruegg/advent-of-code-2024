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
            if (x + 2 < width && y + 2 < height &&
                lines[y][x] == 'M' && lines[y + 1][x + 1] == 'A' && lines[y + 2][x + 2] == 'S' &&
                lines[y + 2][x] == 'M' && lines[y + 1][x + 1] == 'A' && lines[y][x + 2] == 'S'
            ) {
                counter++
            }

            // MAS - SAM
            if (x + 2 < width && y + 2 < height &&
                lines[y][x] == 'M' && lines[y + 1][x + 1] == 'A' && lines[y + 2][x + 2] == 'S' &&
                lines[y + 2][x] == 'S' && lines[y + 1][x + 1] == 'A' && lines[y][x + 2] == 'M'
            ) {
                counter++
            }

            // SAM - MAS
            if (x + 2 < width && y + 2 < height &&
                lines[y][x] == 'S' && lines[y + 1][x + 1] == 'A' && lines[y + 2][x + 2] == 'M' &&
                lines[y + 2][x] == 'M' && lines[y + 1][x + 1] == 'A' && lines[y][x + 2] == 'S'
            ) {
                counter++
            }

            // SAM - SAM
            if (x + 2 < width && y + 2 < height &&
                lines[y][x] == 'S' && lines[y + 1][x + 1] == 'A' && lines[y + 2][x + 2] == 'M' &&
                lines[y + 2][x] == 'S' && lines[y + 1][x + 1] == 'A' && lines[y][x + 2] == 'M'
            ) {
                counter++
            }
        }
    }

    return counter
}