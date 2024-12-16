package aoc16

import aoc12.Coordinate
import java.io.File
import java.util.*

fun main() {
    val input = File("inputs/aoc16/input.txt").readLines()

    val (graph, startNode, endNodes) = parseMaze(input)

    val shortestPathsResult = graph.shortestPathsFrom(startNode)
    val shortestPaths = endNodes.flatMap { shortestPathsResult.getAllShortestPaths(it) }
    val nodesOnShortestPaths = shortestPaths.flatten().map { Coordinate(it.x, it.y) }.distinct()
    printNodes(nodesOnShortestPaths, input)
    println(nodesOnShortestPaths.count())
}

fun printNodes(coordinates: Collection<Coordinate>, input: List<String>) {
    for ((y, line) in input.withIndex()) {
        for ((x, char) in line.withIndex()) {
            if (Coordinate(x, y) in coordinates) {
                print('O')
            } else {
                print(char)
            }
        }
        println()
    }
}

private data class ShortestPathsResult(val distances: Map<Node, Int?>, val predecessors: Map<Node, Set<Node>>) {
    fun getAllShortestPaths(endNode: Node): List<List<Node>> {

        fun collectPath(via: List<Node>): List<List<Node>> {
            val last = via.last()
            val predecessorsOfLast = predecessors[last] ?: error("No path found")
            if (setOf(last) == predecessorsOfLast) {
                return listOf(via.reversed()) // reached start node
            }

            return predecessorsOfLast.flatMap { predecessor ->
                val copiedPath = via.toMutableList()
                copiedPath += predecessor
                collectPath(copiedPath)
            }
        }

        return collectPath(listOf(endNode))
    }
}

private fun Graph.shortestPathsFrom(start: Node): ShortestPathsResult {
    data class NodeWithPriority(val node: Node, val priority: Int)

    val distances = mutableMapOf<Node, Int?>().withDefault { null }
    val queue = PriorityQueue<NodeWithPriority>(Comparator.comparingInt { it.priority })
    val predecessors = mutableMapOf<Node, MutableSet<Node>>()

    distances[start] = 0
    predecessors[start] = mutableSetOf(start) // makes the recursion in getAllShortestPaths easier
    queue += NodeWithPriority(start, 0)

    for (node in nodes) {
        if (node != start) {
            queue += NodeWithPriority(node, Int.MAX_VALUE)
        }
    }

    while (queue.isNotEmpty()) {
        val next = queue.remove()
        for (edge in edges.getOrDefault(next.node, emptySet())) {
            val distanceViaNext = distances[next.node]!! + edge.weight
            val currentDistance = distances[edge.target]
            if (currentDistance == null || distanceViaNext <= currentDistance) {
                if (currentDistance == null || distanceViaNext < currentDistance) {
                    predecessors[edge.target] = mutableSetOf()
                }
                predecessors.getValue(edge.target) += next.node
                distances[edge.target] = distanceViaNext

                // Update priority
                if (queue.removeIf { it.node == edge.target }) {
                    queue += NodeWithPriority(edge.target, distanceViaNext)
                }
            }
        }
    }

    return ShortestPathsResult(distances, predecessors)
}
