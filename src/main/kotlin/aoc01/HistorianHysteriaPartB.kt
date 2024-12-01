package aoc01

import java.io.File

fun main() {
    val inputFile = File("inputs/aoc01/lists.txt")
    val (inputA: List<Int>, inputB: List<Int>) = readInputLists(inputFile)

    val numberToFrequencyInB = inputB
        .groupBy { number -> number }
        .mapValues { (_, groupedNumbers) -> groupedNumbers.size }

    val similarityScore = inputA
        .sumOf { it * (numberToFrequencyInB[it] ?: 0) }

    println(similarityScore)
}
