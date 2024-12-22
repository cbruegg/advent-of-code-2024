package aoc21

import aoc16.AllShortestPathsResult
import aoc16.Edge
import aoc16.Graph
import aoc16.allShortestPathsFrom
import kotlin.math.min

@JvmInline
value class KeypadButton(val label: Char)

data class Keypad(
    val graph: Graph<KeypadButton>,
    val edgeToDirection: Map<Pair<KeypadButton, Edge<KeypadButton>>, Char>
) {
    // Precompute to save resources later
    private val buttonToShortestPathResult = graph.nodes.associateWith { graph.allShortestPathsFrom(it) }
    private val keypadRoutes = mutableMapOf<Pair<KeypadButton, KeypadButton>, List<String>>()

    fun keypadRoute(start: KeypadButton, target: KeypadButton): List<String> {
        return keypadRoutes.getOrPut(start to target) {
            buttonToShortestPathResult.getValue(start).keypadRouteTo(keypad = this, target = target)
                .sortedBy { it.length }
        }
    }

    val cache = mutableMapOf<Triple<KeypadButton, KeypadButton, Int>, Int>()

    fun shortestKeypadRoute(start: KeypadButton, target: KeypadButton, levels: Int): Int {
        val thisLevel = keypadRoute(start, target).map { it + 'A' }
        if (levels == 0) return thisLevel[0].length // sorted by length already

        val cacheKey = Triple(start, target, levels)
        val cached = cache[cacheKey]
        if (cached != null) return cached

        var nextLevel = Int.MAX_VALUE
        for (rawRoute in thisLevel) {
            val route = "A$rawRoute"
            var routeHeads = listOf(0)
            for (i in 0..<route.lastIndex) {
                val a = route[i]
                val b = route[i + 1]
                val tail = shortestKeypadRoute(KeypadButton(a), KeypadButton(b), levels - 1)
                val newRouteHeads = mutableListOf<Int>()
                for (routeHead in routeHeads) {
                    newRouteHeads += routeHead + tail
                }
                routeHeads = newRouteHeads
            }
            nextLevel = min(nextLevel, routeHeads.min())
        }

        cache[cacheKey] = nextLevel

        return nextLevel
    }

    fun allKeypadRoutes(start: KeypadButton, target: KeypadButton, levels: Int): List<String> {
        val thisLevel = keypadRoute(start, target).map { it + 'A' }
        if (levels == 0) return thisLevel

        val nextLevel = mutableListOf<String>()
        for (rawRoute in thisLevel) {
//            val route = rawRoute.surroundCharsWith('A')
            val route = "A$rawRoute"
            var routeHeads = listOf("")
            for (i in 0..<route.lastIndex) {
                val a = route[i]
                val b = route[i + 1]
                val tails = allKeypadRoutes(KeypadButton(a), KeypadButton(b), levels - 1)
                val newRouteHeads = mutableListOf<String>()
                for (routeHead in routeHeads) {
                    for (tail in tails) {
                        newRouteHeads += routeHead + tail
                    }
                }
                routeHeads = newRouteHeads
            }
            nextLevel += routeHeads
        }

        return nextLevel
    }
}

private fun AllShortestPathsResult<KeypadButton>.keypadRouteTo(keypad: Keypad, target: KeypadButton): List<String> {
    return getAllShortestPaths(target)
        .map { path ->
            val sb = StringBuilder()
            for (i in 0..<path.lastIndex) {
                val a = path[i]
                val b = path[i + 1]
                val edge = keypad.graph.edges[a]!!.find { it.target == b }
                val direction = keypad.edgeToDirection[a to edge]!!
                sb.append(direction)
            }
            sb.toString()
        }
}