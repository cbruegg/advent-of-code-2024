package aoc16

import java.io.File
import java.util.*

fun main() {
    val input = File("inputs/aoc16/input.txt").readLines()

    val (graph, startNode, endNodes) = parseMaze(input)

    val (distances, predecessors) = graph.shortestPathFrom(startNode)
    val distancesToEndNodes = endNodes.mapNotNull { distances[it] }
    val minDistance = distancesToEndNodes.min()
    println(minDistance)
}

private data class ShortestPathResult(val distances: Map<Node, Int?>, val predecessors: Map<Node, Node>)

private fun Graph.shortestPathFrom(start: Node): ShortestPathResult {
    data class NodeWithPriority(val node: Node, val priority: Int)

    val distances = mutableMapOf<Node, Int?>().withDefault { null }
    val queue = PriorityQueue<NodeWithPriority>(Comparator.comparingInt { it.priority })
    val predecessors = mutableMapOf<Node, Node>()

    distances[start] = 0
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
            if (currentDistance == null || distanceViaNext < currentDistance) {
                predecessors[edge.target] = next.node
                distances[edge.target] = distanceViaNext

                // Update priority
                if (queue.removeIf { it.node == edge.target }) {
                    queue += NodeWithPriority(edge.target, distanceViaNext)
                }
            }
        }
    }

    return ShortestPathResult(distances, predecessors)
}
