package aoc14

import aoc12.Coordinate

fun main() {
    val input = """
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
    """.trimIndent().lines()
    val width = 11 // 101 in real input
    val height = 7 // 103 in real input

    val regex = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")
    var robotPositions = input.map { robotSpec ->
        val (x, y, veloX, veloY) = regex.find(robotSpec)!!.destructured
        RobotPosition(x.toInt(), y.toInt(), veloX.toInt(), veloY.toInt())
    }

    println(robotPositions, width, height)
    println()
    repeat(100) {
        robotPositions = robotPositions.map { it.next(width, height) }
        println(robotPositions, width, height)
        println()
    }

    println("Safety factor: ${safetyFactorOf(robotPositions, width, height)}")
}

fun safetyFactorOf(robotPositions: List<RobotPosition>, width: Int, height: Int): Int {
    var quadrantA = 0
    var quadrantB = 0
    var quadrantC = 0
    var quadrantD = 0
    for (pos in robotPositions) {
        if (pos.x < width / 2 && pos.y < height / 2) quadrantA++
        if (pos.x > width / 2 && pos.y < height / 2) quadrantB++
        if (pos.x < width / 2 && pos.y > height / 2) quadrantC++
        if (pos.x > width / 2 && pos.y > height / 2) quadrantD++
    }

    return quadrantA * quadrantB * quadrantC * quadrantD
}

data class RobotPosition(val x: Int, val y: Int, val veloX: Int, val veloY: Int) {
    fun next(width: Int, height: Int) =
        copy(
            x = (x + veloX).mod(width),
            y = (y + veloY).mod(height)
        )
}

fun println(positions: List<RobotPosition>, width: Int, height: Int) {
    val coordinateToOccupants = mutableMapOf<Coordinate, Int>()
    for (position in positions) {
        val coordinate = Coordinate(position.x, position.y)
        coordinateToOccupants.merge(coordinate, 1) { a, b -> a + b }
    }

    for (y in 0..<height) {
        for (x in 0..<width) {
            val occupants = coordinateToOccupants.getOrDefault(Coordinate(x, y), 0)
            val occupantsStr = when {
                occupants == 0 -> "."
                occupants < 10 -> occupants.toString()
                else -> ">"
            }
            print(occupantsStr)
        }
        println()
    }
}