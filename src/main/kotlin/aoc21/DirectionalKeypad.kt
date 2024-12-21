package aoc21

import aoc16.Edge
import aoc16.Graph

val directionalKeypad: Keypad = run {
    val edgeToDirection = mutableMapOf<Pair<KeypadButton, Edge<KeypadButton>>, Char>()

    fun edge(source: KeypadButton, target: KeypadButton, direction: Char): Edge<KeypadButton> {
        val edge = Edge(weight = 1, target = target)
        edgeToDirection[source to edge] = direction
        return edge
    }

    val up = KeypadButton('^')
    val ka = KeypadButton('A')
    val left = KeypadButton('<')
    val down = KeypadButton('v')
    val right = KeypadButton('>')

    val directionalKeypadGraph = Graph(
        nodes = setOf(up, ka, left, down, right),
        edges = mapOf(
            left to setOf(edge(left, down, '>')),
            up to setOf(edge(up, ka, '>'), edge(up, down, 'v')),
            down to setOf(edge(down, right, '>'), edge(down, left, '<'), edge(down, up, '^')),
            ka to setOf(edge(ka, right, 'v'), edge(ka, up, '<')),
            right to setOf(edge(right, down, '<'), edge(right, ka, '^')),
        )
    )

    Keypad(directionalKeypadGraph, edgeToDirection)
}