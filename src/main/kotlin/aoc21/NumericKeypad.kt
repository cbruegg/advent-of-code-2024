package aoc21

import aoc16.Edge
import aoc16.Graph

val numericKeypad: Keypad = run {
    val edgeToDirection = mutableMapOf<Pair<KeypadButton, Edge<KeypadButton>>, Char>()

    fun edge(source: KeypadButton, target: KeypadButton, direction: Char): Edge<KeypadButton> {
        val edge = Edge(weight = 1, target = target)
        edgeToDirection[source to edge] = direction
        return edge
    }

    val k7 = KeypadButton('7')
    val k8 = KeypadButton('8')
    val k9 = KeypadButton('9')
    val k4 = KeypadButton('4')
    val k5 = KeypadButton('5')
    val k6 = KeypadButton('6')
    val k1 = KeypadButton('1')
    val k2 = KeypadButton('2')
    val k3 = KeypadButton('3')
    val k0 = KeypadButton('0')
    val ka = KeypadButton('A')
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

    Keypad(numericKeypadGraph, edgeToDirection)
}