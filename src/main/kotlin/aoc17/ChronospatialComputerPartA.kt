package aoc17

import java.io.File

fun main() {
    val input = File("inputs/aoc17/input.txt").readLines()

    val a = input[0].substringAfter(": ").toLong()
    val b = input[1].substringAfter(": ").toLong()
    val c = input[2].substringAfter(": ").toLong()
    val program = input[4].substringAfter(": ").split(',').map { it.toInt() }.toIntArray()

    var firstOut = true
    runComputer(startA = a, startB = b, startC = c, program = program) { outValue ->
        if (!firstOut) {
            print(',')
        }
        print(outValue)
        firstOut = false
        true
    }
    println()
}