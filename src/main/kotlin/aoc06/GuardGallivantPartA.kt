package aoc06

import java.io.File

fun main() {
    val lines = File("inputs/aoc06/input.txt").readLines()

    val width = lines[0].length
    val height = lines.size

    var (obstacles, curX, curY, direction) = parseInitialGameState(lines)

    val visitedPositions = mutableSetOf<Pair<Int, Int>>()
    while (curX in 0..<width && curY in 0..<height) {
        visitedPositions += Pair(curX, curY)

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

    println(visitedPositions.size)
}
