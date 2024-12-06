package aoc06

import java.io.File

fun main() {
    val lines = File("inputs/aoc06/input.txt").readLines()

//    val lines = """
//        ....#.....
//        ....+---+#
//        ....|...|.
//        ..#.|...|.
//        ..+-+-+#|.
//        ..|.|.|.|.
//        .#+-^-+-+.
//        .+----++#.
//        #+----++..
//        ......#O..
//    """.trimIndent().lines()

    val width = lines[0].length
    val height = lines.size

    var loopingGames = 0

    val initialState = parseInitialGameState(lines)
    for (x in 0..<width) {
        for (y in 0..<height) {
            // Must not put new obstacle in starting position:
            if (x != initialState.curX || y != initialState.curY) {
                val gameStateWithNewObstacle = initialState.copy(
                    obstacles = initialState.obstacles + Pair(x, y)
                )
                val simulationResult = simulateGame(width, height, gameStateWithNewObstacle, addedObstacle = Pair(x, y))
                if (simulationResult == SimulationResult.Looped) {
                    loopingGames++
                }
            }
        }
    }

    println(loopingGames)
}

fun simulateGame(width: Int, height: Int, gameState: GuardGameState, addedObstacle: Pair<Int, Int>): SimulationResult {
    var (obstacles, curX, curY, direction) = gameState

    val visitedPositionsWithDirection = mutableSetOf<Triple<Int, Int, Direction>>()

    while (curX in 0..<width && curY in 0..<height) {
        if (!visitedPositionsWithDirection.add(Triple(curX, curY, direction))) {
            return SimulationResult.Looped
        }
        // printState(width, height, curX, curY, direction, addedObstacle, obstacles, visitedPositionsWithDirection)

        val (vecX, vecY) = direction.vector
        val facingPositionX = curX + vecX
        val facingPositionY = curY + vecY

        if (Pair(facingPositionX, facingPositionY) in obstacles) {
            direction = direction.turnRight()
        } else {
            curX = facingPositionX
            curY = facingPositionY
        }
    }

    return SimulationResult.WalkedOut
}

enum class SimulationResult {
    WalkedOut, Looped
}

fun printState(
    width: Int,
    height: Int,
    curX: Int,
    curY: Int,
    direction: Direction,
    addedObstacle: Pair<Int, Int>,
    obstacles: Set<Pair<Int, Int>>,
    visitedPositionsWithDirection: Set<Triple<Int, Int, Direction>>
) {
    for (y in 0..<height) {
        for (x in 0..<width) {
            if (curX == x && curY == y) {
                print(direction.str)
            } else if (Pair(x, y) == addedObstacle) {
                print("O")
            } else if (Pair(x, y) in obstacles) {
                print("#")
            } else {
                val visited = visitedPositionsWithDirection.lastOrNull { it.first == x && it.second == y }
                if (visited != null) {
                    when (visited.third) {
                        Direction.Left, Direction.Right -> print("-")
                        Direction.Up, Direction.Down -> print("|")
                    }
                } else {
                    print(".")
                }
            }
        }
        println()
    }
    println()
}