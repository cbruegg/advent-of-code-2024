package aoc07

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

fun possibleOperatorCombinations(size: Int, operators: Collection<Operator>): Sequence<List<Operator>> =
    when (size) {
        0 -> {
            emptySequence()
        }

        1 -> {
            operators.asSequence().map { listOf(it) }
        }

        else -> {
            // YAY recursion
            val shorterCombinations = possibleOperatorCombinations(size - 1, operators)
            shorterCombinations.flatMap { shorterCombination: List<Operator> ->
                operators.asSequence().map { operator ->
                    shorterCombination + operator
                }
            }
        }
    }

fun Equation.isSolveable(operators: Collection<Operator> = Operator.entries): Boolean =
    possibleOperatorCombinations(operands.size - 1, operators)
        .any { operatorCombination -> this.isSolvedBy(operatorCombination) }

fun Equation.isSolvedBy(operators: List<Operator>): Boolean {
    if (operators.isEmpty()) {
        check(operands.size == 1) { "Not enough operators for ${operands.size} operands!" }
        return result == operands[0]
    }

    check(operands.size >= 2) { "Must have at least two operands!" }

    var actualResult = operators[0].evaluate(operands[0], operands[1])
    operators.asSequence()
        .drop(1) // we already used the first operator
        .forEachIndexed { idx, operator ->
            // idx + 2 as we already evaluated the first operator on the first two operands
            actualResult = operator.evaluate(actualResult, operands[idx + 2])
        }

    return actualResult == result
}


