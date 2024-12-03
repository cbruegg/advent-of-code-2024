package aoc03

import java.io.File

fun main() {
    val inputFile = File("inputs/aoc03/memory.txt")
    val memory = inputFile.readText()

    val mulRegex = Regex("""mul\((\d+),(\d+)\)""")
    val mulCalls = mulRegex.findAll(input = memory)

    val sumOfMultiplications = mulCalls
        .map { match ->
            val (operandAStr, operandBStr) = match.destructured
            val operandA = operandAStr.toInt()
            val operandB = operandBStr.toInt()
            operandA * operandB
        }
        .sum()

    println(sumOfMultiplications)
}