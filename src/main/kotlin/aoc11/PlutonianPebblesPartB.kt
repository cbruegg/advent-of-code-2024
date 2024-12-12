package aoc11

import java.io.File
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val input = File("inputs/aoc11/input.txt").readText().trim()

    val state = input.split(' ').map { it.toLong() }.toLongArray()
    println(state.contentToString())

    println(applyRulesAndCount(state, iterations = 75))
}

inline fun memoize(
    store: MutableMap<Pair<Long, Int>, Long>,
    num: Long,
    iterations: Int,
    crossinline result: () -> Long
): Long {
    val cur = store[num to iterations]
    if (cur != null) {
        return cur
    }

    val value = result()
    store[num to iterations] = value
    return value
}

fun applyRulesAndCountInternal(store: MutableMap<Pair<Long, Int>, Long>, num: Long, iterations: Int): Long =
    memoize(store, num, iterations) {
        if (iterations == 0) {
            return@memoize 1L
        }

        var sum = 0L
        val numOfDigitsInBase10 = num.numOfDigitsInBase10
        when {
            num == 0L -> {
                sum += applyRulesAndCountInternal(store, 1, iterations = iterations - 1)
            }

            numOfDigitsInBase10 % 2 == 0 -> {
                // Do this with number types instead of Strings just for fun
                val moveRightDivisor = 10.0.pow(numOfDigitsInBase10 / 2).toLong()
                val leftHalf = num / moveRightDivisor
                val rightHalf = num - leftHalf * moveRightDivisor
                sum += applyRulesAndCountInternal(store, leftHalf, iterations = iterations - 1)
                sum += applyRulesAndCountInternal(store, rightHalf, iterations = iterations - 1)
            }

            else -> {
                sum += applyRulesAndCountInternal(store, num * 2024, iterations = iterations - 1)
            }
        }

        return@memoize sum
    }

fun applyRulesAndCount(state: LongArray, iterations: Int): Long {
    if (iterations == 0) {
        return state.size.toLong()
    }

    var sum = 0L
    for (i in state.indices) {
        val n = state[i]
        println("Reached index $i")
        sum += applyRulesAndCountInternal(store = mutableMapOf(), num = n, iterations)
    }

    return sum
}

private val Long.numOfDigitsInBase10 get() = log10(this.toDouble()).toInt() + 1
//private val Long.numOfDigitsInBase10 get() = this.toString()