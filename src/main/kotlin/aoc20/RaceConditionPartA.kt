package aoc20

import aoc16.Edge
import aoc16.shortestPathFrom
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

typealias Cheat = Pair<Node, Edge<Node>>
typealias CheatingEdges = Set<Cheat>

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

    val parsed = parseInput(input)
    val cheats = computeCheatingEdges(parsed)
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

private fun computeCheatingEdges(input: Input): CheatingEdges {
    val cheatingEdges = mutableSetOf<Pair<Node, Edge<Node>>>()
    for (node in input.graph.nodes) {
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
    return cheatingEdges
}
