package aoc05

import java.io.File

fun main() {
    val inputFile = File("inputs/aoc05/input.txt")
    val lines = inputFile.readLines()

    // For each page, this contains the set of prerequired pages
    val rules: Map<Int, Set<Int>> = lines
        .asSequence()
        .filter { "|" in it }
        .map { it.split("|") }
        .map { (a, b) -> Pair(a.toInt(), b.toInt()) }
        .groupBy(
            keySelector = { (_, page) -> page },
            valueTransform = { (prerequiredPage, _) -> prerequiredPage }
        )
        .mapValues { (_, prerequiredPages) -> prerequiredPages.toSet() }

    val updates: List<List<Int>> = lines
        .asSequence()
        .filter { "|" !in it && it.isNotBlank() }
        .map { line -> line.split(",").map { it.toInt() } }
        .toList()

    val validUpdates = updates.filter { findViolatedRule(it, rules) == null }
    val sumOfMiddlePages = validUpdates.sumOf { it.middleElement }

    println(sumOfMiddlePages)
}
