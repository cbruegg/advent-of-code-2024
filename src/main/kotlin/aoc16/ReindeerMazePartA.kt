package aoc16

import aoc06.Direction
import java.io.File
import java.util.*

fun main() {
    val input = File("inputs/aoc16/input.txt").readLines()

    val height = input.size
    val width = input[0].length

    val nodes = mutableSetOf<Node>()
    val edges = mutableMapOf<Node, MutableSet<Edge>>()
    var startNode: Node? = null
    var endNodes: Set<Node>? = null // one per direction (all are OK destinations)
    for ((y, line) in input.withIndex()) {
        for ((x, char) in line.withIndex()) {
            when (char) {
                '#' -> {
                    // Wall is an unreachable node -> no-op
                }

                '.', 'S', 'E' -> {
                    val nodeByDirection = Direction.entries.associateWith { direction -> Node(x, y, direction) }
                    nodes += nodeByDirection.values

                    if (char == 'S') {
                        startNode = Node(x, y, Direction.Right)
                    }
                    if (char == 'E') {
                        endNodes = nodeByDirection.values.toSet()
                    }

                    // External edges: Connect virtual left-facing node with left-facing neighbor on left side, etc.
                    for (direction in Direction.entries) {
                        val (vecX, vecY) = direction.vector
                        val neighborX = x + vecX
                        val neighborY = y + vecY
                        if (neighborX in 0..<width && neighborX in 0..<height && input[neighborY][neighborX] != '#') {
                            val edge = Edge(weight = 1, target = Node(neighborX, neighborY, direction))
                            edges.getOrPut(nodeByDirection.getValue(direction), { mutableSetOf() }) += edge
                        }
                    }

                    // Tile-internal edges:
                    // Once on a tile/node, we can turn by 90 degrees -> step from virtual node to another
                    val internallyConnectedDirections = setOf(
                        Direction.Left to Direction.Up,
                        Direction.Left to Direction.Down,
                        Direction.Right to Direction.Up,
                        Direction.Right to Direction.Down,
                    )
                    for ((directionA, directionB) in internallyConnectedDirections) {
                        // Connect both virtual nodes with each other:
                        val edge1 = Edge(weight = 1000, target = Node(x, y, directionB))
                        val edge2 = Edge(weight = 1000, target = Node(x, y, directionA))
                        // virtual node facing into directionA can turn facing directionB
                        edges.getOrPut(nodeByDirection.getValue(directionA), { mutableSetOf() }) += edge1
                        // virtual node facing into directionB can turn facing directionA
                        edges.getOrPut(nodeByDirection.getValue(directionB), { mutableSetOf() }) += edge2
                    }
                }
            }
        }
    }
    check(startNode != null) { "Start node must be set!" }
    check(endNodes != null) { "End node must be set!" }
    val graph = Graph(nodes, edges)

    val (distances, predecessors) = graph.shortestPathFrom(startNode)
    val distancesToEndNodes = endNodes.mapNotNull { distances[it] }
    val minDistance = distancesToEndNodes.min()
    println(minDistance)
}

data class ShortestPathResult(val distances: Map<Node, Int?>, val predecessors: Map<Node, Node>)

fun Graph.shortestPathFrom(start: Node): ShortestPathResult {
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

data class Node(
    val x: Int,
    val y: Int,
    /**
     * In this problem, turning costs 1000 points, so we model each tile
     * as four virtual tiles, i.e. one per walking direction.
     */
    val direction: Direction
)

data class Graph(val nodes: Set<Node>, val edges: Map<Node, Set<Edge>>)

data class Edge(val weight: Int, val target: Node)