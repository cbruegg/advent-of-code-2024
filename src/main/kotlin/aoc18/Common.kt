package aoc18

import aoc12.Coordinate
import aoc16.Edge
import aoc16.Graph

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
