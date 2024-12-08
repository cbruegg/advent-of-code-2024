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
