package aoc07

fun parseEquation(line: String): Equation {
    val parts = line.split(": ")
    val result = parts[0].toLong()
    val operands = parts[1].split(' ').map { it.toLong() }
    return Equation(result, operands)
}

data class Equation(
    val result: Long,
    val operands: List<Long>
)

enum class Operator {
    Add, Mul, Concat
}

fun Operator.evaluate(a: Long, b: Long): Long =
    when (this) {
        Operator.Add -> a + b
        Operator.Mul -> a * b
        Operator.Concat -> (a.toString() + b.toString()).toLong()
    }