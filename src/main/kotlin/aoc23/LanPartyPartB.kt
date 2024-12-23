package aoc23

import java.io.File

fun main() {
    val input = File("inputs/aoc23/input.txt").readLines()
//    val input = """
//        kh-tc
//        qp-kh
//        de-cg
//        ka-co
//        yn-aq
//        qp-ub
//        cg-tb
//        vc-aq
//        tb-ka
//        wh-tc
//        yn-cg
//        kh-ub
//        ta-co
//        de-co
//        tc-td
//        tb-wq
//        wh-td
//        ta-ka
//        td-qp
//        aq-cg
//        wq-ub
//        ub-vc
//        de-ta
//        wq-aq
//        wq-vc
//        wh-yn
//        ka-de
//        kh-ta
//        co-tc
//        wh-qp
//        tb-vc
//        td-yn
//    """.trimIndent().lines()

    val pcsToNeighbors = input
        .asSequence()
        .map { it.split('-') }
        .flatMap { edge -> sequenceOf(edge, edge.reversed()) } // edge is bidirectional
        .groupBy(keySelector = { (a, _) -> a }, valueTransform = { (_, b) -> b })
        .mapValues { (_, v) -> v.toSet() }

    val maxCliques = findMaxCliques(mayInclude = pcsToNeighbors.keys.toMutableSet(), nodeToNeighbors = pcsToNeighbors)
    for (maxClique in maxCliques.sortedByDescending { it.size }) {
        println(maxClique.sorted().joinToString(separator = ","))
    }
}

fun <N> findMaxCliques(
    mustInclude: MutableSet<N> = mutableSetOf(),
    mayInclude: MutableSet<N>,
    mustNotInclude: MutableSet<N> = mutableSetOf(),
    nodeToNeighbors: Map<N, Set<N>>
): Set<Set<N>> {
    if (mayInclude.isEmpty() && mustNotInclude.isEmpty()) {
        return setOf(mustInclude)
    }

    val cliques = mutableSetOf<Set<N>>()
    while (mayInclude.isNotEmpty()) {
        val node = mayInclude.first()

        val must = mustInclude.toMutableSet()
        must += node
        val may = mayInclude.toMutableSet()
        may.retainAll(nodeToNeighbors[node] ?: emptySet())
        val mustNot = mustNotInclude.toMutableSet()
        mustNot.retainAll(nodeToNeighbors[node] ?: emptySet())
        cliques += findMaxCliques(must, may, mustNot, nodeToNeighbors)

        mayInclude -= node
        mustNotInclude += node
    }
    return cliques
}