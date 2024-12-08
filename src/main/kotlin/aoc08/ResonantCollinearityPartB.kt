package aoc08

import java.io.File

fun main() {
    val lines = File("inputs/aoc08/map.txt").readLines()

//    val lines = """
//        ............
//        ........0...
//        .....0......
//        .......0....
//        ....0.......
//        ......A.....
//        ............
//        ............
//        ........A...
//        .........A..
//        ............
//        ............
//    """.trimIndent().lines()

    val signals = parseSignals(lines)
    val groupedAntennas: Map<Char, List<Antenna>> = signals.antennas.groupBy { it.symbol }

    val antinodes = mutableSetOf<Vector>()
    for ((symbol, antennas) in groupedAntennas) {
        if (antennas.size > 1) {
            // If there is more than one antenna of a given symbol,
            // each antenna of this symbol is on a line with another antenna,
            // making them all antinodes
            antennas.forEach { antinodes += it.locationVector }
        }

        for (antennaA in antennas) {
            for (antennaB in antennas) {
                if (antennaA == antennaB) continue

                val vecAB = Vector(
                    antennaB.x - antennaA.x,
                    antennaB.y - antennaA.y
                )

                // Route from B on the line A to B
                var antinodeFromAViaB = antennaB.locationVector.apply(vecAB)
                while (antinodeFromAViaB.isInBounds(signals.xBounds, signals.yBounds)) {
                    antinodes += antinodeFromAViaB
                    antinodeFromAViaB = antinodeFromAViaB.apply(vecAB)
                }

                // Route from A on the line B to A
                var antinodeFromBViaA = antennaA.locationVector.apply(vecAB.invert())
                while (antinodeFromBViaA.isInBounds(signals.xBounds, signals.yBounds)) {
                    antinodes += antinodeFromBViaA
                    antinodeFromBViaA = antinodeFromBViaA.apply(vecAB.invert())
                }
            }
        }
    }

    renderMap(lines, antinodes)

    println(antinodes.size)
}
