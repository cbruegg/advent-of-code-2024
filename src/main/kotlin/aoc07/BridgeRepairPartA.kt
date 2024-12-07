package aoc07

import java.io.File

fun main() {
    val lines = File("inputs/aoc07/equations.txt").readLines()

    val equations = lines.map { parseEquation(it) }

    val sum = equations
        .filter { it.isSolveable(operators = setOf(Operator.Add, Operator.Mul)) }
        .sumOf { it.result }

    println(sum)
}
