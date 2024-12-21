package aoc21

import aoc16.AllShortestPathsResult
import aoc16.Edge
import aoc16.Graph
import aoc16.allShortestPathsFrom

data class KeypadButton(val label: Char)

data class Keypad(
    val graph: Graph<KeypadButton>,
    val edgeToDirection: MutableMap<Pair<KeypadButton, Edge<KeypadButton>>, Char>
) {
    // Precompute to save resources later
    private val buttonToShortestPathResult = graph.nodes.associateWith { graph.allShortestPathsFrom(it) }
    private val keypadRoutes = mutableMapOf<Pair<KeypadButton, KeypadButton>, List<String>>()

    fun keypadRoute(start: KeypadButton, target: KeypadButton): List<String> {
        return keypadRoutes.getOrPut(start to target) {
            buttonToShortestPathResult.getValue(start).keypadRouteTo(keypad = this, target = target)
        }
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