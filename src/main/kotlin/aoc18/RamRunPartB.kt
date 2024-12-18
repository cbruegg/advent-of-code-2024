package aoc18

import aoc12.Coordinate
import aoc16.shortestPathFrom
import java.io.File

const val mapSize = 71 // 7 for example, 71 for real input

fun main() {
    val input = File("inputs/aoc18/input.txt").readLines()
//    val input = """
//        5,4
//        4,2
//        4,5
//        3,0
//        2,1
//        6,3
//        2,4
//        1,5
//        0,6
//        3,3
//        2,6
//        5,1
//        1,2
//        5,5
//        2,5
//        6,5
//        1,4
//        0,4
//        6,4
//        1,1
//        6,1
//        1,0
//        0,5
//        1,6
//        2,0
//    """.trimIndent().lines()

    val maxInputLengthWithRoute = maxInputLengthWithRoute(input)
    val firstBadByte = input[maxInputLengthWithRoute]
    check(!hasRouteWithInputLength(input, maxInputLengthWithRoute + 1)) {
        "Should not have route with input length ${maxInputLengthWithRoute + 1}"
    }
    println("maxInputLengthWithRoute=$maxInputLengthWithRoute")
    println("firstBadByte=$firstBadByte")
}

fun maxInputLengthWithRoute(input: List<String>, lowerBound: Int = 0, upperBound: Int = input.size, depth: Int = 0): Int {
    println("${"  ".repeat(depth)}maxInputLengthWithRoute(input, lowerBound = $lowerBound, upperBound = $upperBound)")
    if (lowerBound + 1 == upperBound) {
        return if (hasRouteWithInputLength(input, lowerBound)) {
            lowerBound
        } else {
            -1
        }
    }

    val middle = lowerBound + ((upperBound - lowerBound) / 2)
    val hasRouteInMiddle = hasRouteWithInputLength(input, middle)

    return if (hasRouteInMiddle) {
        maxInputLengthWithRoute(input, lowerBound = middle, upperBound = upperBound, depth = depth + 1)
    } else {
        maxInputLengthWithRoute(input, lowerBound = lowerBound, upperBound = middle, depth = depth + 1)
    }
}

fun hasRouteWithInputLength(input: List<String>, inputLength: Int): Boolean {
    // true = has obstacle
    val map = Array(mapSize) { BooleanArray(mapSize) }
    for (line in input.take(inputLength)) {
        val (xStr, yStr) = line.split(',')
        map[yStr.toInt()][xStr.toInt()] = true
    }

    val graph = mapToGraph(map)
    val start = Coordinate(0, 0)
    val end = Coordinate(mapSize - 1, mapSize - 1)
    val startToEndDistance = graph.shortestPathFrom(start).distances[end]
    return startToEndDistance != null
}