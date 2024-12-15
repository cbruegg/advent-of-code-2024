package aoc13

fun main() {
    val input = """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent()

    val games = parseClawGames(input)
    val solutionsByGame = games.map { it.solve() }
    val cheapestSolutions = solutionsByGame.map { solutions -> solutions.minByOrNull { it.cost } }
    val totalCost = cheapestSolutions.sumOf { it?.cost ?: 0 }
    println(totalCost)
}

fun parseClawGames(input: String): List<ClawGame> {
    val xDeltaRegex = Regex("""X\+(\d+),""")
    val yDeltaRegex = Regex("""Y\+(\d+)""")
    val prizeXRegex = Regex("""X=(\d+),""")
    val prizeYRegex = Regex("""Y=(\d+)""")
    val sections = input.split("\n\n")
    return sections.map { section ->
        val sectionLines = section.lines()
        val buttonASpec = sectionLines[0]
        val buttonBSpec = sectionLines[1]
        val prizeSpec = sectionLines[2]

        val xDeltaOfA = xDeltaRegex.find(buttonASpec)!!.groupValues[1].toInt()
        val yDeltaOfA = yDeltaRegex.find(buttonASpec)!!.groupValues[1].toInt()
        val xDeltaOfB = xDeltaRegex.find(buttonBSpec)!!.groupValues[1].toInt()
        val yDeltaOfB = yDeltaRegex.find(buttonBSpec)!!.groupValues[1].toInt()
        val prizeX = prizeXRegex.find(prizeSpec)!!.groupValues[1].toInt()
        val prizeY = prizeYRegex.find(prizeSpec)!!.groupValues[1].toInt()
        ClawGame(xDeltaOfA, yDeltaOfA, xDeltaOfB, yDeltaOfB, prizeX, prizeY)
    }
}

data class ClawGameSolution(val buttonAPresses: Int, val buttonBPresses: Int)

val ClawGameSolution.cost get() = buttonAPresses * 3 + buttonBPresses

fun ClawGameSolution.verifyAgainst(clawGame: ClawGame) {
    val actualX = buttonAPresses * clawGame.xDeltaOfA + buttonBPresses * clawGame.xDeltaOfB
    val actualY = buttonAPresses * clawGame.yDeltaOfA + buttonBPresses * clawGame.yDeltaOfB
    check(clawGame.prizeX == actualX) { "Expected x=${clawGame.prizeX}, was $actualX" }
    check(clawGame.prizeX == actualX) { "Expected y=${clawGame.prizeY}, was $actualY" }
}

fun ClawGame.solve(): List<ClawGameSolution> {
    // https://www.wolframalpha.com/input?i=solve+for+a%2C+b%3A+a+*+x_1+%2B+b+*+x_2+%3D+x_3+%2C+a+*+y_1+%2B+b+*+y_2+%3D+y_3
    // a := buttonAPresses
    // b := buttonBPresses
    // x_1 := xDeltaOfA
    // y_1 := yDeltaOfA
    // x_2 := xDeltaOfB
    // y_2 := yDeltaOfB
    // x_3 := prizeX
    // y_3 := prizeY

    val solutions = mutableListOf<ClawGameSolution>()

    // a = (x_3 y_2 - x_2 y_3)/(x_1 y_2 - x_2 y_1) and b = (x_3 y_1 - x_1 y_3)/(x_2 y_1 - x_1 y_2) and x_2 y_1!=x_1 y_2 and y_2 !=0
    if (xDeltaOfB * yDeltaOfA != xDeltaOfA * yDeltaOfB && yDeltaOfB != 0) {
        val buttonAPresses = (prizeX * yDeltaOfB - xDeltaOfB * prizeY).toDouble() / (xDeltaOfA * yDeltaOfB - xDeltaOfB * yDeltaOfA)
        val buttonBPresses = (prizeX * yDeltaOfA - xDeltaOfA * prizeY).toDouble() / (xDeltaOfB * yDeltaOfA - xDeltaOfA * yDeltaOfB)
        if (buttonAPresses.isRound && buttonBPresses.isRound) {
            solutions += ClawGameSolution(
                buttonAPresses = buttonAPresses.toInt(),
                buttonBPresses = buttonBPresses.toInt()
            ).also { it.verifyAgainst(this) }
        }
    }

    // a = y_3/y_1 and b = (x_3 y_1 - x_1 y_3)/(x_2 y_1) and y_2 = 0 and y_1 !=0 and x_2 !=0
    if (yDeltaOfB == 0 && yDeltaOfA != 0 && xDeltaOfB != 0) {
        val buttonAPresses = prizeY.toDouble() / yDeltaOfA
        val buttonBPresses = (prizeX * yDeltaOfA - xDeltaOfA * prizeY).toDouble() / (xDeltaOfB * yDeltaOfA)
        if (buttonAPresses.isRound && buttonBPresses.isRound) {
            solutions += ClawGameSolution(
                buttonAPresses = buttonAPresses.toInt(),
                buttonBPresses = buttonBPresses.toInt()
            ).also { it.verifyAgainst(this) }
        }
    }

    // b = (x_3 - a x_1)/x_2 and y_3 = 0 and y_2 = 0 and y_1 = 0 and x_2 !=0
    if (prizeY == 0 && yDeltaOfB == 0 && yDeltaOfA == 0 && xDeltaOfB != 0) {
        // We can pick an arbitrary `a` here: b = (x_3 - a x_1)/x_2
        // As button A is more expensive than button B, we just set a := 0
        val buttonAPresses = 0
        val buttonBPresses = prizeX.toDouble() / xDeltaOfB
        if (buttonBPresses.isRound) {
            solutions += ClawGameSolution(
                buttonAPresses = buttonAPresses,
                buttonBPresses = buttonBPresses.toInt()
            ).also { it.verifyAgainst(this) }
        }
    }

    // b = -(a y_1)/y_2 and y_3 = 0 and y_2 !=0 and x_3 = 0 and x_1 = (x_2 y_1)/y_2
    // This case cannot happen as we assume all numbers are in N_0 (natural numbers)

    // b = (y_3 - a y_1)/y_2 and y_3 !=0 and x_2 = (x_3 y_2)/y_3 and x_1 = (x_3 y_1)/y_3 and y_2 !=0
    if (prizeY != 0 && xDeltaOfB == (prizeX * yDeltaOfB) / prizeY && xDeltaOfA == (prizeX * yDeltaOfA) / prizeY && yDeltaOfB != 0) {
        // We can pick an arbitrary `a` here: b = (y_3 - a y_1)/y_2
        // As button A is more expensive than button B, we just set a := 0
        val buttonAPresses = 0
        val buttonBPresses = prizeY.toDouble() / yDeltaOfB
        if (buttonBPresses.isRound) {
            solutions += ClawGameSolution(
                buttonAPresses = buttonAPresses,
                buttonBPresses = buttonBPresses.toInt()
            ).also { it.verifyAgainst(this) }
        }
    }

    return solutions
}

data class ClawGame(
    val xDeltaOfA: Int,
    val yDeltaOfA: Int,
    val xDeltaOfB: Int,
    val yDeltaOfB: Int,
    val prizeX: Int,
    val prizeY: Int
)

private val Double.isRound: Boolean
    get() = toInt().toDouble() == this