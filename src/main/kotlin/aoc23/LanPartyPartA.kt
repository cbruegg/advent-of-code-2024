package aoc23

import java.io.File

fun main() {
    val input = File("inputs/aoc23/input.txt").readLines()

    val pcsToNeighbors = input
        .asSequence()
        .map { it.split('-') }
        .flatMap { edge -> sequenceOf(edge, edge.reversed()) } // edge is bidirectional
        .groupBy(keySelector = { (a, _) -> a }, valueTransform = { (_, b) -> b })
        .mapValues { (_, v) -> v.toSet() }

    val cliquesOf3 = mutableSetOf<List<String>>()
    for ((pc, neighbors) in pcsToNeighbors) {
        if (!pc.startsWith('t')) continue

        for (neighbor in neighbors) {
            for (neighborOfNeighbor in pcsToNeighbors[neighbor] ?: emptyList()) {
                if (neighborOfNeighbor != pc && neighborOfNeighbor in neighbors) {
                    cliquesOf3 += listOf(pc, neighbor, neighborOfNeighbor).sorted()
                }
            }
        }
    }
    for (clique in cliquesOf3.sortedBy { it.joinToString(separator = "") }) {
        println(clique)
    }
    println(cliquesOf3.size)
}