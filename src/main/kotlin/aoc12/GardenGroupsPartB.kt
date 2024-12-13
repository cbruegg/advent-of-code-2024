package aoc12

import java.io.File

fun main() {
    val map = File("inputs/aoc12/map.txt").readLines()
//    val map = """
//    AAAA
//    BBCD
//    BBCC
//    EEEC
//""".trimIndent().lines()

    val height = map.size
    val width = map[0].length

    val coordinateQueue = mutableSetOf<Coordinate>()
    for (y in 0..<height) {
        for (x in 0..<width) {
            coordinateQueue += Coordinate(x, y)
        }
    }

    val regions = mutableSetOf<Region>()

    var nextCoordinate = coordinateQueue.removeFirst()
    while (nextCoordinate != null) {
        val region = visitRegion(map, nextCoordinate)
        regions += region
        coordinateQueue -= region.coordinates // remove visited
        nextCoordinate = coordinateQueue.removeFirst()
    }

    println("regions=$regions")

    val regionsToArea = regions.groupBy(keySelector = { it.symbol }) { it.area }
    val regionsToPerimeter = regions.groupBy(keySelector = { it.symbol }) { it.computePerimeter() }
    val totalFencePrice = regions.sumOf { it.computeFencePrice() }
    println("regionsToArea=$regionsToArea")
    println("regionsToPerimeter=$regionsToPerimeter")
    println("totalFencePrice=$totalFencePrice")
}

private fun Region.computeFencePrice(): Int = area * computeNumberOfSides()