package aoc12

fun main() {
    val map = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent().lines()

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

fun visitRegion(map: List<String>, start: Coordinate): Region {
    val height = map.size
    val width = map[0].length

    val symbol = map[start.y][start.x]
    val coordinates = mutableSetOf<Coordinate>()

    fun visit(x: Int, y: Int) {
        if (x in 0..<width && y in 0..<height && map[y][x] == symbol) {
            if (coordinates.add(Coordinate(x, y))) {
                visit(x + 1, y)
                visit(x - 1, y)
                visit(x, y + 1)
                visit(x, y - 1)
            } // else we already visited this plot
        }
    }

    visit(start.x, start.y)

    return Region(symbol, coordinates)
}

fun <E> MutableSet<E>.removeFirst(): E? {
    val first = firstOrNull() ?: return null
    this -= first
    return first
}

data class Coordinate(val x: Int, val y: Int) {
    val leftNeighbor get() = Coordinate(x - 1, y)
    val topNeighbor get() = Coordinate(x, y - 1)
    val rightNeighbor get() = Coordinate(x + 1, y)
    val bottomNeighbor get() = Coordinate(x, y + 1)
}

data class Region(val symbol: Char, val coordinates: Set<Coordinate>) {
    val area get() = coordinates.size

    fun computePerimeter(): Int {
        var perimeter = 0
        for (coordinate in coordinates) {
            if (coordinate.leftNeighbor !in coordinates) perimeter++
            if (coordinate.topNeighbor !in coordinates) perimeter++
            if (coordinate.rightNeighbor !in coordinates) perimeter++
            if (coordinate.bottomNeighbor !in coordinates) perimeter++
        }
        return perimeter
    }

    fun computeFencePrice(): Int = area * computePerimeter()
}