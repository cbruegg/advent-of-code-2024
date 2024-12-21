package aoc20

import aoc12.Coordinate
import aoc16.Edge
import aoc16.ShortestPathResult
import aoc16.shortestPathFrom
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
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

    val shortcuts = computeShortcuts(parsed, route, cheatDuration = 20).takeShortest()
    val tested = AtomicInteger(0)
    val cheatsThatSaveAtLeast100 = shortcuts.parallelStream()
        .filter { (start, end, length) ->
            val curTested = tested.incrementAndGet()
            if (curTested % 1000 == 0) {
                println("Testing shortcut $curTested of ${shortcuts.size} (${(curTested.toDouble() * 100 / shortcuts.size).toInt()} %)")
            }

            // This assumes shortcut starts and ends on route
            val normalCostShortcutStartToShortcutEnd = shortestPathResult.distances[end]!! - shortestPathResult.distances[start]!!

            val timeSave = normalCostShortcutStartToShortcutEnd - length
            timeSave >= 100
        }
        .count()

    println("cheatsThatSaveAtLeast100=$cheatsThatSaveAtLeast100")
}

data class Shortcut(val start: Node, val end: Node, val length: Int)

fun computeShortcuts(input: Input, route: List<Node>, cheatDuration: Int): Sequence<Shortcut> = sequence {
    for (i in route.indices) {
        println("${i + 1} / ${route.size}")
        for (j in (i + 2)..route.lastIndex) {
//            println("i=$i, j=$j")
            val start = route[i]
            val end = route[j]
            val length = start.coordinate.distanceTo(end.coordinate)
            if (length != -1 && length <= cheatDuration) {
                yield(Shortcut(start, end, length))
            }
        }
    }
}

private fun Coordinate.distanceTo(other: Coordinate): Int {
    return abs(x - other.x) + abs(y - other.y)
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
