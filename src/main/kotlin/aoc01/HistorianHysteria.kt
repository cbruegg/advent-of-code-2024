package aoc01

import kotlin.math.abs

fun main() {
    val inputA = listOf(3, 4, 2, 1, 3, 3)
    val inputB = listOf(4, 3, 5, 3, 9, 3)

    val sortedA = inputA.sorted()
    val sortedB = inputB.sorted()

    val sumOfDifferences = sortedA.zip(sortedB).sumOf { (a, b) -> abs(a - b) }
    println(sumOfDifferences)
}