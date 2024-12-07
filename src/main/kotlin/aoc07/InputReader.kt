package aoc07

fun parseEquation(line: String): Equation {
    val parts = line.split(": ")
    val result = parts[0].toInt()
    val operands = parts[1].split(' ').map { it.toInt() }
    return Equation(result, operands)
}

data class Equation(
    val result: Int,
    val operands: List<Int>
)

enum class Operator {
    Add, Mul
}

fun Operator.evaluate(a: Int, b: Int): Int =
    when (this) {
        Operator.Add -> a + b
        Operator.Mul -> a * b
    }