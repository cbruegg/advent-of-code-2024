package aoc20

import aoc12.Coordinate
import aoc16.Edge
import aoc16.Graph
import aoc16.shortestPathFrom
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

suspend fun main() {
    val input = File("inputs/aoc20/input.txt").readLines()
//    val input = """
//        ###############
//        #...#...#.....#
//        #.#.#.#.#.###.#
//        #S#...#.#.#...#
//        #######.#.#.###
//        #######.#.#...#
//        #######.#.###.#
//        ###..E#...#...#
//        ###.#######.###
//        #...###...#...#
//        #.#####.#.###.#
//        #.#...#.#.#...#
//        #.#.#.#.#.#.###
//        #...#...#...###
//        ###############
//    """.trimIndent().lines()

    val (parsed, cheats) = parseInput(input)
    val lengthWithoutCheats = parsed.graph.shortestPathFrom(parsed.start).distances[parsed.end] ?: error("No route!")
    val cheatsByTimeSave = mutableMapOf<Int, MutableSet<Cheat>>()

    coroutineScope {
        val processedCheats = AtomicInteger(0)
        cheats
            .map { cheat ->
                async {
                    val (sourceNode, edge) = cheat
                    val edgesWithCheat = parsed.graph.edges.toMutableMap().apply {
                        this[sourceNode] = (this[sourceNode] ?: emptySet()) + edge
                    }
                    val updatedGraph = parsed.graph.copy(edges = edgesWithCheat)
                    val lengthWithCheat = updatedGraph.shortestPathFrom(parsed.start).distances[parsed.end]
                        ?: error("No route with cheat should not happen!")

                    val processed = processedCheats.incrementAndGet()
                    println("Processed ${processed + 1} of ${cheats.size} cheats")

                    val timeSave = lengthWithoutCheats - lengthWithCheat
                    Pair(timeSave, cheat)
                }
            }
            .awaitAll()
            .forEach { (timeSave, cheat) ->
                cheatsByTimeSave.getOrPut(timeSave, { mutableSetOf() }) += cheat
            }
    }

    cheatsByTimeSave.entries
        .sortedByDescending { (timeSave, _) -> timeSave }
        .forEach { (timeSave, cheats) -> println("Saves $timeSave: ${cheats.size} cheat(s)") }

    val cheatsThatSaveAtLeast100 = cheatsByTimeSave.entries
        .filter { (timeSave, _) -> timeSave >= 100 }
        .sumOf { (_, cheats) -> cheats.size }

    println("cheatsThatSaveAtLeast100=$cheatsThatSaveAtLeast100")
}

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

fun findShortestRoute(
    input: Input,
    cheats: Int,
    currentDistance: Int = 0,
    start: Node = input.start,
    visited: Set<Node> = emptySet()
): Int? {
    if (start == input.end) return currentDistance

    return start.neighborsIn(input)
        .mapNotNull { neighbor ->
            val needsCheat = start.isWall || neighbor.isWall
//            println("neighbor=$neighbor, currentDistance=$currentDistance, cheats = $cheats, visited=$visited")
            if ((!needsCheat || cheats > 0) && neighbor !in visited) {
                findShortestRoute(
                    input,
                    start = neighbor,
                    cheats = if (needsCheat) max(0, cheats - 1) else cheats,
                    currentDistance = currentDistance + 1,
                    visited = visited + neighbor
                )
            } else {
                null
            }
        }
        .minOrNull()
}

typealias Cheat = Pair<Node, Edge<Node>>
typealias CheatingEdges = Set<Cheat>

fun parseInput(map: List<String>): Pair<Input, CheatingEdges> {
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
    val input = Input(
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
    val cheatingEdges = mutableSetOf<Pair<Node, Edge<Node>>>()
    for (node in nodes) {
        if (node.isWall) {
            val neighbors = node.neighborsIn(input)
            for (neighborA in neighbors) {
                for (neighborB in neighbors) {
                    if (neighborA != neighborB && !neighborA.isWall && !neighborB.isWall) {
                        cheatingEdges += neighborA to Edge(weight = 2, neighborB)
                    }
                }
            }
        }
    }

    return Pair(input, cheatingEdges)
}
