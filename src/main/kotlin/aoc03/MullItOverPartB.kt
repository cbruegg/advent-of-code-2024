package aoc03

import java.io.File

fun main() {
    val inputFile = File("inputs/aoc03/memory.txt")
    val memory = inputFile.readLines().joinToString(separator = "")
    // Delete everything between don't and do (or end of line = $)
    // .*? eats everything between these markers non-greedily
    val memoryWithoutDisabledSections = memory.replace(
        Regex("""don't\(\).*?($|do\(\))"""),
        ""
    )

    val mulRegex = Regex("""mul\((\d+),(\d+)\)""")
    val mulCalls = mulRegex.findAll(input = memoryWithoutDisabledSections)

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