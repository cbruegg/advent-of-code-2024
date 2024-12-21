package aoc21

import aoc16.Edge
import aoc16.Graph
import aoc16.ShortestPathResult
import aoc16.shortestPathFrom

fun main() {
    val directions = computeDirectionsForNumericKeypad("029A")
    println(directions)
}

data class NumericKeypadButton(val label: Char)

data class NumericKeypad(
    val graph: Graph<NumericKeypadButton>,
    val edgeToDirection: MutableMap<Pair<NumericKeypadButton, Edge<NumericKeypadButton>>, Char>
) {
    // Precompute to save resources later
    private val buttonToShortestPathResult = graph.nodes.associateWith { graph.shortestPathFrom(it) }

    fun shortestPathsFrom(button: NumericKeypadButton) = buttonToShortestPathResult.getValue(button)
}

val numericKeypad: NumericKeypad = run {
    val edgeToDirection = mutableMapOf<Pair<NumericKeypadButton, Edge<NumericKeypadButton>>, Char>()

    fun edge(source: NumericKeypadButton, target: NumericKeypadButton, direction: Char): Edge<NumericKeypadButton> {
        val edge = Edge(weight = 1, target = target)
        edgeToDirection[source to edge] = direction
        return edge
    }

    val k7 = NumericKeypadButton('7')
    val k8 = NumericKeypadButton('8')
    val k9 = NumericKeypadButton('9')
    val k4 = NumericKeypadButton('4')
    val k5 = NumericKeypadButton('5')
    val k6 = NumericKeypadButton('6')
    val k1 = NumericKeypadButton('1')
    val k2 = NumericKeypadButton('2')
    val k3 = NumericKeypadButton('3')
    val k0 = NumericKeypadButton('0')
    val ka = NumericKeypadButton('A')
    val numericKeypadGraph = Graph(
        nodes = setOf(k7, k8, k9, k4, k5, k6, k1, k2, k3, k0, ka),
        edges = mapOf(
            k7 to setOf(edge(k7, k8, '>'), edge(k7, k4, 'v')),
            k4 to setOf(edge(k4, k5, '>'), edge(k4, k1, 'v'), edge(k4, k7, '^')),
            k1 to setOf(edge(k1, k2, '>'), edge(k1, k4, '^')),
            k8 to setOf(edge(k8, k9, '>'), edge(k8, k5, 'v'), edge(k8, k7, '<')),
            k5 to setOf(edge(k5, k6, '>'), edge(k5, k2, 'v'), edge(k5, k4, '<'), edge(k5, k8, '^')),
            k2 to setOf(edge(k2, k3, '>'), edge(k2, k0, 'v'), edge(k2, k1, '<'), edge(k2, k5, '^')),
            k0 to setOf(edge(k0, ka, '>'), edge(k0, k2, '^')),
            k9 to setOf(edge(k9, k6, 'v'), edge(k9, k8, '<')),
            k6 to setOf(edge(k6, k3, 'v'), edge(k6, k5, '<'), edge(k6, k9, '^')),
            k3 to setOf(edge(k3, ka, 'v'), edge(k3, k2, '<'), edge(k3, k6, '^')),
            ka to setOf(edge(ka, k0, '<'), edge(ka, k3, '^')),
        )
    )

    NumericKeypad(numericKeypadGraph, edgeToDirection)
}

fun computeDirectionsForNumericKeypad(code: String): String {
    var pos = NumericKeypadButton('A')
    val route = StringBuilder()
    for (next in code) {
        val routeToNext = numericKeypad.shortestPathsFrom(pos).keypadRouteTo(NumericKeypadButton(next))
        route.append(routeToNext)
        route.append('A') // push the button
        pos = NumericKeypadButton(next)
    }
    return route.toString()
}

fun ShortestPathResult<NumericKeypadButton>.keypadRouteTo(target: NumericKeypadButton): String {
    val reverseRoute = StringBuilder()
    var cur = target
    while (true) {
        val predecessor = predecessors[cur] ?: break
        val edgePredecessorToCur = Edge(weight = 1, cur)
        reverseRoute.append(numericKeypad.edgeToDirection.getValue(predecessor to edgePredecessorToCur))
        cur = predecessor
    }
    return reverseRoute.reversed().toString()
}