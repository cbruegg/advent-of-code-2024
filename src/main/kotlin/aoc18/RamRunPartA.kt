package aoc18

import aoc12.Coordinate
import aoc16.Edge
import aoc16.Graph
import aoc16.shortestPathFrom

fun main() {
    val input = """
        5,4
        4,2
        4,5
        3,0
        2,1
        6,3
        2,4
        1,5
        0,6
        3,3
        2,6
        5,1
        1,2
        5,5
        2,5
        6,5
        1,4
        0,4
        6,4
        1,1
        6,1
        1,0
        0,5
        1,6
        2,0
    """.trimIndent().lines().take(12)
    val len = 7

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
    for ((y, line) in map.withIndex()) {
        for ((x, hasObstacle) in line.withIndex()) {
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
