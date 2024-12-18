package aoc18

import aoc12.Coordinate
import aoc16.Edge
import aoc16.Graph
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

fun printMap(map: Array<BooleanArray>) {
    for (line in map) {
        for (hasObstacle in line) {
            print(if (hasObstacle) '#' else '.')
        }
        println()
    }
}

fun mapToGraph(map: Array<BooleanArray>): Graph<Coordinate> {
    val nodes = mutableSetOf<Coordinate>()
    val edges = mutableMapOf<Coordinate, MutableSet<Edge<Coordinate>>>()
    for ((y, line) in map.withIndex()) {
        for ((x, hasObstacle) in line.withIndex()) {
            val node = Coordinate(x, y)
            nodes += node

            if (!hasObstacle) {
                node.neighbors.forEach { neighbor ->
                    if (neighbor.y in map.indices && neighbor.x in map[y].indices) {
                        val neighborHasObstacle = map[neighbor.y][neighbor.x]
                        if (!neighborHasObstacle) {
                            edges.computeIfAbsent(node, { mutableSetOf() }) += Edge(weight = 1, target = neighbor)
                            edges.computeIfAbsent(neighbor, { mutableSetOf() }) += Edge(weight = 1, target = node)
                        }
                    }
                }
            }
        }
    }
    return Graph(nodes, edges)
}
