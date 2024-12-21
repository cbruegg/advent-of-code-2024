package aoc21

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

    fun shortestPathsFrom(button: KeypadButton) = buttonToShortestPathResult.getValue(button)
}