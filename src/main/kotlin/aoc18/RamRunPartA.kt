package aoc18

import aoc12.Coordinate
import aoc16.shortestPathFrom
import java.io.File

fun main() {
    val input = File("inputs/aoc18/input.txt").readLines().take(1024)
    val len = 71

    // true = has obstacle
    val map = Array(len) { BooleanArray(len) }
    for (line in input) {
        val (xStr, yStr) = line.split(',')
        map[yStr.toInt()][xStr.toInt()] = true
    }

    printMap(map)

    val graph = mapToGraph(map)
    val start = Coordinate(0, 0)
    val end = Coordinate(len - 1, len - 1)
    val startToEndDistance = graph.shortestPathFrom(start).distances[end] ?: error("No route from start to end")
    println(startToEndDistance)
}
