package aoc12

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

    private val maximumX by lazy { coordinates.maxOf { it.x } }
    private val maximumY by lazy { coordinates.maxOf { it.y } }

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

    fun computeNumberOfSides(): Int {
        data class FencePart(val coordinate: Coordinate, val side: Side)

        val fenceParts = mutableSetOf<FencePart>()
        for (coordinate in coordinates) {
            if (coordinate.hasLeftFence) fenceParts += FencePart(coordinate, Side.Left)
            if (coordinate.hasTopFence) fenceParts += FencePart(coordinate, Side.Top)
            if (coordinate.hasRightFence) fenceParts += FencePart(coordinate, Side.Right)
            if (coordinate.hasBottomFence) fenceParts += FencePart(coordinate, Side.Bottom)
        }

        fun visit(x: Int, y: Int, side: Side, connectedFenceParts: MutableSet<FencePart>) {
            if (x in 0..maximumX && y in 0..maximumY && FencePart(Coordinate(x, y), side) in fenceParts) {
                if (connectedFenceParts.add(FencePart(Coordinate(x, y), side))) {
                    visit(x + 1, y, side, connectedFenceParts)
                    visit(x - 1, y, side, connectedFenceParts)
                    visit(x, y + 1, side, connectedFenceParts)
                    visit(x, y - 1, side, connectedFenceParts)
                } // else we already visited this plot
            }
        }

        var sides = 0
        val fencePartsLeftToProcess = fenceParts.toMutableSet()
        var nextFencePart = fencePartsLeftToProcess.removeFirst()
        while (nextFencePart != null) {
            val connectedFenceParts = mutableSetOf<FencePart>()
            visit(nextFencePart.coordinate.x, nextFencePart.coordinate.y, nextFencePart.side, connectedFenceParts)
            fencePartsLeftToProcess -= connectedFenceParts
            sides++
            nextFencePart = fencePartsLeftToProcess.removeFirst()
        }

        return sides
    }

    private val Coordinate.hasLeftFence get() = leftNeighbor !in coordinates
    private val Coordinate.hasTopFence get() = topNeighbor !in coordinates
    private val Coordinate.hasRightFence get() = rightNeighbor !in coordinates
    private val Coordinate.hasBottomFence get() = bottomNeighbor !in coordinates
}

enum class Side {
    Left, Top, Right, Bottom
}