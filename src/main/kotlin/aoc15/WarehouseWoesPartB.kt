package aoc15

import aoc12.Coordinate

fun main() {
    val input = """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent()

    var gameState = input.substringBefore("\n\n").lines()
    gameState = adaptGameForPartB(gameState)
    val moves = input.substringAfter("\n\n").replace("\n", "")

    printGameState(gameState)
    for (move in moves) {
        gameState = advanceGameState(gameState, move)
        printGameState(gameState)
    }

    val sumOfGpsCoordinates = findGpsCoordinates(gameState).sum()
    println("sumOfGpsCoordinates=$sumOfGpsCoordinates")
}

fun adaptGameForPartB(gameState: List<String>): List<String> =
    gameState.map { line ->
        line.replace("#", "##")
            .replace("O", "[]")
            .replace(".", "..")
            .replace("@", "@.")
    }

private fun printGameState(gameState: List<String>) {
    gameState.forEach { println(it) }
    println()
}

private fun findGpsCoordinates(gameState: List<String>): Sequence<Int> =
    sequence {
        for ((y, line) in gameState.withIndex()) {
            for ((x, char) in line.withIndex()) {
                if (char == '[') {
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

    val robotNextY = robotY + vecY
    val robotNextX = robotX + vecX
    val hasObstacle = gameState[robotNextY][robotNextX] != '.'
    if (!hasObstacle || applyForce(nextGameState, robotNextX, robotNextY, vecX, vecY)) {
        nextGameState[robotY] = nextGameState[robotY].replaceIndex(robotX, '.')
        nextGameState[robotNextY] = nextGameState[robotNextY].replaceIndex(robotNextX, '@')
    } // else obstacles reach the wall -> take no action

    return nextGameState
}

fun applyForce(nextGameState: MutableList<String>, x: Int, y: Int, vecX: Int, vecY: Int): Boolean {
    val here = nextGameState[y][x]
    if (here == '.') return true // Force only needs to be applied to objects
    if (here == '#') return false // Reached the wall

    val isLeft = here == '['
    val leftX = if (isLeft) x else x - 1
    val rightX = leftX + 1

    val nextLeftX = leftX + vecX
    val nextRightX = rightX + vecX
    val nextY = y + vecY

    check(vecX == 0 || vecY == 0) { "Can only move in one direction at a time" }
    val appliedForceToObstacle = when (vecX) {
        // on x-axis, we only need to free one tile (left/right) as we're a 2x1 obstacle
        -1 -> applyForce(nextGameState, nextLeftX, nextY, vecX, vecY)
        1 -> applyForce(nextGameState, nextRightX, nextY, vecX, vecY)
        // else we're moving on y-axis -> need to free TWO tiles
        else -> applyForce(nextGameState, nextLeftX, nextY, vecX, vecY) &&
                applyForce(nextGameState, nextRightX, nextY, vecX, vecY)
    }

    if (appliedForceToObstacle) {
        // We successfully applied force to the objects on the tiles we'll occupy next,
        // or there were none there. Either way, at this point the space is free,
        // and we can occupy it.
        nextGameState[y] = nextGameState[y].replaceIndex(leftX, '.')
        nextGameState[y] = nextGameState[y].replaceIndex(rightX, '.')
        nextGameState[nextY] = nextGameState[nextY].replaceIndex(nextLeftX, '[')
        nextGameState[nextY] = nextGameState[nextY].replaceIndex(nextRightX, ']')
        return true
    } else {
        // One of the objects we needed to push to make space for ourselves couldn't be moved
        return false
    }
}
