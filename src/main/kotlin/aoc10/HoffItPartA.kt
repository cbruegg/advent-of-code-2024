package aoc10

fun main() {
    val input = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent().lines()
    //  val input = File("inputs/aoc10/map.txt").readLines()

    val map: List<IntArray> = input.map { line -> line.map { char -> char.digitToInt() }.toIntArray() }
    val height = map.size
    val width = map[0].size
    val zeroPositions = mutableListOf<Pair<Int, Int>>()
    for (y in 0..<height) {
        for (x in 0..<width) {
            if (map[y][x] == 0) zeroPositions += Pair(x, y)
        }
    }
    val sumOfReachableNines = zeroPositions.sumOf { (x, y) -> countReachableNines(x, y, map, width, height) }
    println(sumOfReachableNines)
}

fun countReachableNines(
    startX: Int,
    startY: Int,
    map: List<IntArray>,
    width: Int,
    height: Int,
    visited: MutableSet<Pair<Int, Int>> = mutableSetOf()
): Int {
    if (!visited.add(Pair(startX, startY))) {
        // We already visited this location, so there's no more peaks to be found
        return 0
    }
//    println("Visiting ($startX, $startY) with visited=$visited")

    val curValue = map[startY][startX]
    if (curValue == 9) {
        return 1
    } else {
        val upValue = if (startY + 1 < height) map[startY + 1][startX] else -1
        val downValue = if (startY - 1 >= 0) map[startY - 1][startX] else -1
        val rightValue = if (startX + 1 < width) map[startY][startX + 1] else -1
        val leftValue = if (startX - 1 >= 0) map[startY][startX - 1] else -1
        val reachableFromUp =
            if (upValue == curValue + 1) countReachableNines(startX, startY + 1, map, width, height, visited) else 0
        val reachableFromDown =
            if (downValue == curValue + 1) countReachableNines(startX, startY - 1, map, width, height, visited) else 0
        val reachableFromRight =
            if (rightValue == curValue + 1) countReachableNines(startX + 1, startY, map, width, height, visited) else 0
        val reachableFromLeft =
            if (leftValue == curValue + 1) countReachableNines(startX - 1, startY, map, width, height, visited) else 0

        return reachableFromUp + reachableFromDown + reachableFromRight + reachableFromLeft
    }
}