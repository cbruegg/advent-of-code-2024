package aoc14

import aoc12.Coordinate


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