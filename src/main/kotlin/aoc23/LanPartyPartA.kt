package aoc23

fun main() {
    val input = """
        kh-tc
        qp-kh
        de-cg
        ka-co
        yn-aq
        qp-ub
        cg-tb
        vc-aq
        tb-ka
        wh-tc
        yn-cg
        kh-ub
        ta-co
        de-co
        tc-td
        tb-wq
        wh-td
        ta-ka
        td-qp
        aq-cg
        wq-ub
        ub-vc
        de-ta
        wq-aq
        wq-vc
        wh-yn
        ka-de
        kh-ta
        co-tc
        wh-qp
        tb-vc
        td-yn
    """.trimIndent().lines()

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
}