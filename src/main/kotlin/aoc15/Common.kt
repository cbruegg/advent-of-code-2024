package aoc15

import aoc12.Coordinate


fun Coordinate.moveBy(vecX: Int, vecY: Int): Coordinate =
    Coordinate(x + vecX, y + vecY)

fun String.replaceIndex(i: Int, c: Char): String =
    replaceRange(i, i + 1, "$c")