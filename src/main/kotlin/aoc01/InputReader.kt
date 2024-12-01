package aoc01

import java.io.File

fun readInput(file: File): Pair<List<Int>, List<Int>> {
    val listA = mutableListOf<Int>()
    val listB = mutableListOf<Int>()
    file.useLines { lines ->
        lines.forEach { line ->
            val (a, b) = line.split("   ").map { it.toInt() }
            listA += a
            listB += b
        }
    }
    return Pair(listA, listB)
}