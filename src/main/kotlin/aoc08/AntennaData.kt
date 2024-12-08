package aoc08


fun renderMap(lines: List<String>, antinodes: Set<Vector>) {
    val height = lines.size
    val width = lines[0].length
    for (y in 0..<height) {
        for (x in 0..<width) {
            if (Vector(x, y) in antinodes) {
                print('#')
            } else {
                print(lines[y][x])
            }
        }
        println()
    }
}

data class Signals(
    val width: Int,
    val height: Int,
    val antennas: List<Antenna>
) {
    val xBounds = 0..<width
    val yBounds = 0..<height
}

data class Vector(val x: Int, val y: Int) {
    fun invert() = Vector(-x, -y)
    fun apply(other: Vector) = Vector(x + other.x, y + other.y)
    fun isInBounds(xBound: IntRange, yBound: IntRange) = x in xBound && y in yBound
}

data class Antenna(val x: Int, val y: Int, val symbol: Char) {
    val locationVector get() = Vector(x, y)
}

fun parseSignals(lines: List<String>): Signals {
    val height = lines.size
    val width = lines[0].length
    val antennas = mutableListOf<Antenna>()
    for (y in 0..<height) {
        for (x in 0..<width) {
            val symbol = lines[y][x]
            if (symbol != '.') {
                antennas += Antenna(x, y, symbol)
            }
        }
    }
    return Signals(width, height, antennas)
}