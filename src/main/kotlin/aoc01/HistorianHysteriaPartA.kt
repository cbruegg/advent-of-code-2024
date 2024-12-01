package aoc01

import java.io.File
import kotlin.math.abs

fun main() {
    val inputFile = File("inputs/aoc01/lists.txt")
    val (inputA, inputB) = readInputLists(inputFile)

    val sortedA: List<Int> = inputA.sorted()
    val sortedB: List<Int> = inputB.sorted()

    val sumOfDifferences = sortedA
        .zip(sortedB)
        .sumOf { (a, b) -> abs(a - b) }
    println(sumOfDifferences)
}
