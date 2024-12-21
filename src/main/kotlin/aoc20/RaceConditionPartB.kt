package aoc20

import aoc16.Edge
import aoc16.ShortestPathResult
import aoc16.shortestPathFrom
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

fun main() {
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
    val shortestPathResult = parsed.graph.shortestPathFrom(parsed.start)
    val lengthWithoutCheats = shortestPathResult.distances[parsed.end] ?: error("No route!")
    val route = shortestPathResult.routeTo(parsed.end)

    val shortcuts = computeShortcuts2(parsed, route, cheatDuration = 20).takeShortest()
    val tested = AtomicInteger(0)
    val cheatsThatSaveAtLeast100 = shortcuts.parallelStream()
        .filter { (start, end, length) ->
            val curTested = tested.incrementAndGet()
            if (curTested % 1000 == 0) {
                println("Testing shortcut $curTested of ${shortcuts.size} (${(curTested.toDouble() * 100 / shortcuts.size).toInt()} %)")
            }
            val cheatEdge = Edge(weight = length, end)
            val edgesWithCheat = parsed.graph.edges.toMutableMap().apply {
                this[start] = (this[start] ?: emptySet()) + cheatEdge
            }
            val updatedGraph = parsed.graph.copy(edges = edgesWithCheat)
            val lengthWithCheat = updatedGraph.shortestPathFrom(parsed.start).distances[parsed.end]
                ?: error("No route with cheat should not happen!")

            val timeSave = lengthWithoutCheats - lengthWithCheat
            timeSave >= 100
        }
        .count()

//    val cheatsThatSaveAtLeast100 = computeCheatingEdges(parsed, route, cheatDurationLeft = 20)
//        .count { cheat ->
//            val edgesWithCheat = parsed.graph.edges.toMutableMap().apply {
//                for ((sourceNode, edge) in cheat) {
//                    this[sourceNode] = (this[sourceNode] ?: emptySet()) + edge
//                }
//            }
//            val updatedGraph = parsed.graph.copy(edges = edgesWithCheat)
//            val lengthWithCheat = updatedGraph.shortestPathFrom(parsed.start).distances[parsed.end]
//                ?: error("No route with cheat should not happen!")
//
//            val timeSave = lengthWithoutCheats - lengthWithCheat
//            timeSave >= 100
//        }

    println("cheatsThatSaveAtLeast100=$cheatsThatSaveAtLeast100")
}

data class Shortcut(val start: Node, val end: Node, val length: Int)

fun computeShortcuts2(input: Input, route: List<Node>, cheatDuration: Int): Sequence<Shortcut> = sequence {
    for (i in 0..route.lastIndex - 2) {
        val shortcutEndCandidates = route.subList(i + 2, route.size).toSet()
        // TODO Ignore shortcuts that don't actually shorten the route
        val start = route[i]
        val shortcutsFromHere = input.graphWithCheatingEdges.shortestPathFrom(start)
            .distances
            .asSequence()
            .filter { (_, distance) -> distance != null && distance <= cheatDuration }
            .filter { (end, _) -> !end.isWall }
            .filter { (end, _) -> end in shortcutEndCandidates } // TODO: NOT SURE IF THIS IS CORRECT!
            .map { (end, distance) -> Shortcut(start, end, distance!!) }
        yieldAll(shortcutsFromHere)
    }
}

fun computeShortcuts(input: Input, route: List<Node>, cheatDuration: Int): Sequence<Shortcut> = sequence {
    for (i in route.indices) {
        for (j in (i + 2)..route.lastIndex) {
            println("i=$i, j=$j")
            val start = route[i]
            val end = route[j]
            val length = computeShortcutLength(input, start, end, cheatDuration)
            if (length != -1) {
                yield(Shortcut(start, end, length))
            }
        }
    }
}

fun computeShortcutLength(input: Input, start: Node, end: Node, cheatDurationLeft: Int): Int {
    if (start == end) return 0
    if (cheatDurationLeft == 0) return -1

    val minLength = start.neighborsIn(input)
        .mapNotNull { neighbor ->
            computeShortcutLength(input, start = neighbor, end = end, cheatDurationLeft = cheatDurationLeft - 1)
                .takeIf { it != -1 }
        }
        .minOrNull()

    return minLength?.let { it + 1 } ?: -1
}

fun Sequence<Shortcut>.takeShortest(): List<Shortcut> {
    val shortest = mutableMapOf<Pair<Node, Node>, Int>()
    for (shortcut in this@takeShortest) {
        val (start, end, length) = shortcut
        shortest.compute(start to end) { _, otherLength -> min(length, otherLength ?: Int.MAX_VALUE) }
    }
    return shortest.entries.map { (startEnd, length) -> Shortcut(startEnd.first, startEnd.second, length) }
}

fun <N> ShortestPathResult<N>.routeTo(end: N): List<N> {
    val reverseRoute = mutableListOf(end)
    var cur = end
    while (true) {
        val predecessor = predecessors[cur] ?: break
        reverseRoute += predecessor
        cur = predecessor
    }
    return reverseRoute.reversed()
}

private typealias LongCheat = List<Pair<Node, Edge<Node>>>

private fun computeCheatingEdges(input: Input, route: List<Node>, cheatDurationLeft: Int): Sequence<LongCheat> =
    sequence {
        for (i in route.indices) {
            yieldAll(computeCheatingEdges(input, route, cheatDurationLeft, cheatStartLocation = i))
        }
    }

// TODO Cache?
private fun computeCheatingEdges(
    input: Input,
    route: List<Node>,
    cheatDurationLeft: Int,
    cheatStartLocation: Int,
    visitedByCheat: Set<Node> = setOf(route[cheatStartLocation]),
    startNode: Node = route[cheatStartLocation]
): Sequence<LongCheat> =
    sequence {
        if (route.lastIndex == cheatStartLocation || cheatDurationLeft == 0) return@sequence

        for (neighbor in startNode.neighborsIn(input)) {
            // TODO Abort if cheat cost paid is <= timeSave
            // Cheat isn't worth it if we go back on the route or just one step ahead
            if (neighbor in route.subList(0, cheatStartLocation + 2)) continue
            // Don't introduce a cycle into the cheat
            if (neighbor in visitedByCheat) continue

            val cheatHead = listOf(startNode to Edge(weight = 1, neighbor))
            if (!neighbor.isWall) {
                yield(cheatHead)
            }
            for (cheatTail in computeCheatingEdges(
                input,
                route,
                cheatDurationLeft - 1,
                cheatStartLocation,
                visitedByCheat + neighbor,
                startNode = neighbor
            )) {
                yield(cheatHead + cheatTail)
            }
        }
    }.distinctBy { Pair(it.firstOrNull(), it.lastOrNull()) }
