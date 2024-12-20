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

    val sumOfCombinationsForDesigns = designs.sumOf {
        println("Processing design $it...")
        computePossibleCombinations(it, towels)
    }
    println(sumOfCombinationsForDesigns)
}

fun computePossibleCombinations(
    design: String,
    towels: List<String>,
    cache: MutableMap<String, Long> = mutableMapOf()
): Long {
    if (design.isEmpty()) return 0

    val cachedResult = cache[design]
    if (cachedResult != null) return cachedResult

    var possibleCombinations = 0L
    for (towel in towels) {
        if (!design.startsWith(towel)) continue // towel is not suitable
        if (design.length == towel.length) {
            possibleCombinations++
        } else {
            val restOfDesign = design.substring(towel.length)
            possibleCombinations += computePossibleCombinations(restOfDesign, towels, cache)
        }
    }
    cache[design] = possibleCombinations
    return possibleCombinations
}