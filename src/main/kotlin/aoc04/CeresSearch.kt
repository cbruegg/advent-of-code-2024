package aoc04

fun main() {
    val lines = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent().split("\n")

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
            if (y + 3 < height &&
                (lines[y][x] == 'X' && lines[y + 1][x] == 'M' &&
                        lines[y + 2][x] == 'A' && lines[y + 3][x] == 'S' ||
                        lines[y][x] == 'S' && lines[y + 1][x] == 'A' &&
                        lines[y + 2][x] == 'M' && lines[y + 3][x] == 'X')
            ) {
                counter++
            }

            // diagonal (top-left to bottom-right)
            if (x + 3 < width && y + 3 < height &&
                (lines[y][x] == 'X' && lines[y + 1][x + 1] == 'M' &&
                        lines[y + 2][x + 2] == 'A' && lines[y + 3][x + 3] == 'S' ||
                        lines[y][x] == 'S' && lines[y + 1][x + 1] == 'A' &&
                        lines[y + 2][x + 2] == 'M' && lines[y + 3][x + 3] == 'X')
            ) {
                counter++
            }

            // diagonal (top-right to bottom-left)
            if (x - 3 >= 0 && y + 3 < height &&
                (lines[y][x] == 'X' && lines[y + 1][x - 1] == 'M' &&
                        lines[y + 2][x - 2] == 'A' && lines[y + 3][x - 3] == 'S' ||
                        lines[y][x] == 'S' && lines[y + 1][x - 1] == 'A' &&
                        lines[y + 2][x - 2] == 'M' && lines[y + 3][x - 3] == 'X')
            ) {
                counter++
            }
        }
    }

    return counter
}