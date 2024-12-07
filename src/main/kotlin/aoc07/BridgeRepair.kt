package aoc07

fun main() {
    val lines = """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
    """.trimIndent().lines()

    val equations = lines.map { parseEquation(it) }

    val sum = equations
        .filter { it.isSolveable() }
        .sumOf { it.result }

    println(sum)
}

fun Equation.isSolveable(): Boolean =
    possibleOperatorCombinations(operands.size - 1)
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

fun possibleOperatorCombinations(size: Int): Sequence<List<Operator>> =
    when (size) {
        0 -> {
            emptySequence()
        }

        1 -> {
            Operator.entries.asSequence().map { listOf(it) }
        }

        else -> {
            // YAY recursion
            val shorterCombinations = possibleOperatorCombinations(size - 1)
            shorterCombinations.flatMap { shorterCombination: List<Operator> ->
                Operator.entries.asSequence().map { operator ->
                    shorterCombination + operator
                }
            }
        }
    }