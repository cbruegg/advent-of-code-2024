package aoc14

import aoc12.Coordinate
import java.io.File

fun main() {
    val input = File("inputs/aoc14/input.txt").readLines()
    val width = 101 // 101 in real input, 11 in example
    val height = 103 // 103 in real input, 7 in example

    val regex = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")
    var robotPositions = input.map { robotSpec ->
        val (x, y, veloX, veloY) = regex.find(robotSpec)!!.destructured
        RobotPosition(x.toInt(), y.toInt(), veloX.toInt(), veloY.toInt())
    }

    println(robotPositions, width, height)
    println()
    repeat(10000) { i ->
        robotPositions = robotPositions.map { it.next(width, height) }
        if (robotPositions.distinctBy { Coordinate(it.x, it.y) }.size == robotPositions.size) {
            println("--- AFTER ${i + 1} SECONDS ---")
            println(robotPositions, width, height)
            println()
            readln()
        }
    }

    println("Safety factor: ${safetyFactorOf(robotPositions, width, height)}")
}
