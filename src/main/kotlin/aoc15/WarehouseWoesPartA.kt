package aoc15

import aoc12.Coordinate
import java.io.File

fun main() {
    val input = File("inputs/aoc15/input.txt").readText()

    var gameState = input.substringBefore("\n\n").lines()
    val moves = input.substringAfter("\n\n").replace("\n", "")

    printGameState(gameState)
    for (move in moves) {
        gameState = advanceGameState(gameState, move)
        printGameState(gameState)
    }

    val sumOfGpsCoordinates = findGpsCoordinates(gameState).sum()
    println("sumOfGpsCoordinates=$sumOfGpsCoordinates")
}

private fun printGameState(gameState: List<String>) {
    gameState.forEach { println(it) }
    println()
}

private fun findGpsCoordinates(gameState: List<String>): Sequence<Int> =
    sequence {
        for ((y, line) in gameState.withIndex()) {
            for ((x, char) in line.withIndex()) {
                if (char == 'O') {
                    yield(100 * y + x)
                }
            }
        }
    }

private fun advanceGameState(gameState: List<String>, move: Char): List<String> {
    val (robotX, robotY) = gameState
        .withIndex()
        .map { (y, line) -> Coordinate(line.indexOf('@'), y) }
        .first { (x, _) -> x != -1 }

    val nextGameState = gameState.toMutableList()

    val (vecX, vecY) = when (move) {
        '^' -> Pair(0, -1)
        'v' -> Pair(0, 1)
        '<' -> Pair(-1, 0)
        '>' -> Pair(1, 0)
        else -> error("Unexpected movement command: $move")
    }

    val obstacles = findObstacles(gameState, robotX, robotY, vecX, vecY)
    if (obstacles != null) {
        if (obstacles.isNotEmpty()) {
            // Push all obstacles one unit into the direction (vecX, vecY)
            val firstObstacle = obstacles.first()
            val positionAfterLastObstacle = obstacles.last().moveBy(vecX, vecY)
            nextGameState[firstObstacle.y] = nextGameState[firstObstacle.y].replaceIndex(firstObstacle.x, '.')
            nextGameState[positionAfterLastObstacle.y] =
                nextGameState[positionAfterLastObstacle.y].replaceIndex(positionAfterLastObstacle.x, 'O')
        }

        nextGameState[robotY] = nextGameState[robotY].replaceIndex(robotX, '.')
        nextGameState[robotY + vecY] = nextGameState[robotY + vecY].replaceIndex(robotX + vecX, '@')
    } // else obstacles reach the wall -> take no action

    return nextGameState
}


/**
 * @return `null` if obstacles reach the wall
 */
private fun findObstacles(gameState: List<String>, robotX: Int, robotY: Int, vecX: Int, vecY: Int): List<Coordinate>? {
    var curX = robotX
    var curY = robotY
    val obstacles = mutableListOf<Coordinate>()
    while (true) {
        curX += vecX
        curY += vecY
        when (gameState[curY][curX]) {
            '#' -> return null
            'O' -> obstacles += Coordinate(curX, curY)
            '.' -> return obstacles
        }
    }
}