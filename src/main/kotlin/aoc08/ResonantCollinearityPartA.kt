package aoc08

import java.io.File

fun main() {
    val lines = File("inputs/aoc08/map.txt").readLines()

    val signals = parseSignals(lines)
    val groupedAntennas: Map<Char, List<Antenna>> = signals.antennas.groupBy { it.symbol }

    val antinodes = mutableSetOf<Vector>()
    for ((symbol, antennas) in groupedAntennas) {
        for (antennaA in antennas) {
            for (antennaB in antennas) {
                if (antennaA == antennaB) continue

                val vecAB = Vector(
                    antennaB.x - antennaA.x,
                    antennaB.y - antennaA.y
                )
                val antinodeFromAViaB = antennaB.locationVector.apply(vecAB)
                val antinodeFromBViaA = antennaA.locationVector.apply(vecAB.invert())
                if (antinodeFromAViaB.isInBounds(signals.xBounds, signals.yBounds)) {
                    antinodes += antinodeFromAViaB
                }
                if (antinodeFromBViaA.isInBounds(signals.xBounds, signals.yBounds)) {
                    antinodes += antinodeFromBViaA
                }
            }
        }
    }

    renderMap(lines, antinodes)

    println(antinodes.size)
}

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