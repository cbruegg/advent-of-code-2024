package aoc25

import java.io.File

fun main() {
    val input = File("inputs/aoc25/input.txt").readText()

//    val input = """
//        #####
//        .####
//        .####
//        .####
//        .#.#.
//        .#...
//        .....
//
//        #####
//        ##.##
//        .#.##
//        ...##
//        ...#.
//        ...#.
//        .....
//
//        .....
//        #....
//        #....
//        #...#
//        #.#.#
//        #.###
//        #####
//
//        .....
//        .....
//        #.#..
//        ###..
//        ###.#
//        ###.#
//        #####
//
//        .....
//        .....
//        .....
//        #....
//        #.#..
//        #.#.#
//        #####
//    """.trimIndent()

    val schematics = input.split("\n\n").map { it.lines() }
    val locks = schematics
        .filter { schematic -> schematic.first().all { it == '#' } }
    val keys = schematics
        .filter { schematic -> schematic.last().all { it == '#' } }
    println(locks)
    println(keys)

    val height = locks[0].size - 1

    var fits = 0
    for (lock in locks) {
        for (key in keys) {
            val lockHeights = barHeights(lock)
            val keyHeights = barHeights(key)
            val lockFits = lockHeights.zip(keyHeights)
                .all { (lockHeight, keyHeight) -> lockHeight + keyHeight < height }
            if (lockFits) fits++
        }
    }
    println(fits)
}

fun barHeights(schematic: List<String>): IntArray {
    val height = schematic.size
    val width = schematic[0].length
    val heights = IntArray(width)
    for (x in 0..<width) {
        var barHeight = -1 // Subtract top/bottom line
        for (y in 0..<height) {
            if (schematic[y][x] == '#') barHeight++
        }
        heights[x] = barHeight
    }
    return heights
}

fun keyFits(lock: List<String>, key: List<String>): Boolean {
    if (lock.size != key.size) return false
    for ((lockLine, keyLine) in lock.zip(key)) {
        for ((l, k) in lockLine.zip(keyLine)) {
            if (l == k) return false
        }
    }
    return true
}