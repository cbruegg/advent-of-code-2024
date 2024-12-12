package aoc11

import java.io.File
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val input = File("inputs/aoc11/input.txt").readText().trim()

    var state: List<Long> = input.split(' ').map { it.toLong() }
    println(state)
    repeat(25) {
        val nextState = ArrayList<Long>(/* initialCapacity = */ (state.size * 1.5).toInt())
        for (n in state) {
            val numOfDigitsInBase10 = n.numOfDigitsInBase10
            when {
                n == 0L -> {
                    nextState += 1
                }

                numOfDigitsInBase10 % 2 == 0 -> {
                    // Do this with number types instead of Strings just for fun
                    val moveRightDivisor = 10.0.pow(numOfDigitsInBase10 / 2).toLong()
                    val leftHalf = n / moveRightDivisor
                    val rightHalf = n - leftHalf * moveRightDivisor
                    nextState += leftHalf
                    nextState += rightHalf
                }

                else -> {
                    nextState += n * 2024
                }
            }
        }
        state = nextState
        // println(state)
    }
    println(state.size)
}

private val Long.numOfDigitsInBase10 get() = log10(this.toDouble()).toInt() + 1