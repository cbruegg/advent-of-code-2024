package aoc11

import java.io.File
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val input = File("inputs/aoc11/input.txt").readText().trim()

    var state = input.split(' ').map { it.toLong() }.toLongList()
    println(state)
    repeat(75) { i ->
        println("Iteration $i: ${state.size} elements")
        val nextState = LongList(initialCapacity = (state.size * 1.5).toInt())
        state.forEach { n ->
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

class LongList(initialCapacity: Int) {
    private var arr = LongArray(initialCapacity)
    var size = 0
        private set

    operator fun plusAssign(l: Long) {
        if (size == arr.size) {
            arr = arr.copyOf((size * 1.5).toInt())
        }

        arr[size++] = l
    }

    fun forEach(consumer: (Long) -> Unit) {
        for (i in 0..<size) {
            consumer(arr[i])
        }
    }

    override fun toString() = arr.contentToString()
}

fun List<Long>.toLongList(): LongList {
    val longList = LongList(size)
    forEach { longList += it }
    return longList
}

private val Long.numOfDigitsInBase10 get() = log10(this.toDouble()).toInt() + 1