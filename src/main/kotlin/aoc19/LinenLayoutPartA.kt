package aoc19

import java.io.File

fun main() {
//    val input = """
//        r, wr, b, g, bwu, rb, gb, br
//
//        brwrr
//        bggr
//        gbbr
//        rrbgbr
//        ubwu
//        bwurrg
//        brgr
//        bbrgwb
//    """.trimIndent().lines()
    val input = File("inputs/aoc19/input.txt").readLines()

    val towels = input[0].split(", ").sortedByDescending { it.length }
    val designs = input.drop(2)

    val possibleDesigns = designs.filter { isDesignPossible(it, towels) }
    println(possibleDesigns.size)
}

fun isDesignPossible(design: String, towels: List<String>): Boolean {
    if (design.isEmpty()) return true

    for (towel in towels) {
        if (design.startsWith(towel) && isDesignPossible(design.substring(towel.length), towels)) {
            return true
        }
    }
    return false
}