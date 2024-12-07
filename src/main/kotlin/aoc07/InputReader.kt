package aoc07

fun parseEquation(line: String): Equation {
    val parts = line.split(": ")
    val result = parts[0].toLong()
    val operands = parts[1].split(' ').map { it.toLong() }
    return Equation(result, operands)
}
