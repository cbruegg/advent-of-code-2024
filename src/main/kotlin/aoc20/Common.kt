package aoc20

import aoc12.Coordinate
import aoc16.Edge
import aoc16.Graph

data class Node(val x: Int, val y: Int, val isWall: Boolean) {
    val coordinate get() = Coordinate(x, y)

    fun neighborsIn(input: Input): List<Node> =
        coordinate.neighbors.mapNotNull { input.nodeByCoordinate[it] }
}

data class Input(
    val graph: Graph<Node>,
    val start: Node,
    val end: Node,
    val width: Int,
    val height: Int,
    val nodeByCoordinate: Map<Coordinate, Node>
)

fun parseInput(map: List<String>): Input {
    var start: Node? = null
    var end: Node? = null
    val nodes = mutableSetOf<Node>()
    val edges = mutableMapOf<Node, MutableSet<Edge<Node>>>()
    for ((y, line) in map.withIndex()) {
        for ((x, char) in line.withIndex()) {
            val hasWall = char == '#'
            val node = Node(x, y, hasWall)
            nodes += node
            if (char == 'S') start = node
            if (char == 'E') end = node

            if (!hasWall) {
                node.coordinate.neighbors.forEach { neighbor ->
                    if (neighbor.y in map.indices && neighbor.x in map[y].indices) {
                        val neighborIsWall = map[neighbor.y][neighbor.x] == '#'
                        if (!neighborIsWall) {
                            val neighborNode = Node(neighbor.x, neighbor.y, neighborIsWall)
                            edges.computeIfAbsent(node, { mutableSetOf() }) += Edge(weight = 1, target = neighborNode)
                            edges.computeIfAbsent(neighborNode, { mutableSetOf() }) += Edge(weight = 1, target = node)
                        }
                    }
                }
            }
        }
    }
    return Input(
        Graph(
            nodes,
            edges
        ),
        start ?: error("No start found"),
        end ?: error("No end found"),
        width = map[0].length,
        height = map.size,
        nodeByCoordinate = nodes.associateBy { it.coordinate }
    )
}

typealias Cheat = Pair<Node, Edge<Node>>
typealias CheatingEdges = Set<Cheat>