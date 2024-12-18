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

data class ShortestPathResult<N>(val distances: Map<N, Int?>, val predecessors: Map<N, N>)

fun <N> Graph<N>.shortestPathFrom(start: N): ShortestPathResult<N> {
    data class NodeWithPriority(val node: N, val priority: Int)

    val distances = mutableMapOf<N, Int?>().withDefault { null }
    val queue = PriorityQueue<NodeWithPriority>(Comparator.comparingInt { it.priority })
    val predecessors = mutableMapOf<N, N>()

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
            val distanceToNext = distances[next.node] ?: continue // no route!

            val distanceViaNext = distanceToNext + edge.weight
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
