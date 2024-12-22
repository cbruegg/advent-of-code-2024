package aoc22

import java.io.File

fun main() {
//    val input = """
//        1
//        10
//        100
//        2024
//    """.trimIndent().lines()
    val input = File("inputs/aoc22/input.txt").readLines()

    val sumOf2000thSecret = input.map { it.toLong() }.sumOf { seed -> prng(seed).drop(1999).first() }
    println(sumOf2000thSecret)
}

