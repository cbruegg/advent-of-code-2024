package aoc16

import aoc06.Direction

data class Maze(
    val graph: Graph<Node>,
    val startNode: Node,
    val endNodes: Set<Node>
)

fun parseMaze(input: List<String>): Maze {
    val height = input.size
    val width = input[0].length

    val nodes = mutableSetOf<Node>()
    val edges = mutableMapOf<Node, MutableSet<Edge<Node>>>()
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

    return Maze(graph, startNode, endNodes)
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

data class Graph<N>(val nodes: Set<N>, val edges: Map<N, Set<Edge<N>>>)

data class Edge<N>(val weight: Int, val target: N)