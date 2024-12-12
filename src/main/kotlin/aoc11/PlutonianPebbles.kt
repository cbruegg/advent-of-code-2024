package aoc11

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.bufferedWriter
import kotlin.io.path.forEachLine
import kotlin.io.path.writeText
import kotlin.math.pow

fun main() {
    val input = File("inputs/aoc11/input.txt")
    // replace spaces with newlines to be able to read line by line later
    val inputLines = input.readText().split(' ').joinToString(separator = "\n")

    val stateFile = Files.createTempFile("pebbles", ".txt")
    stateFile.writeText(inputLines)
    val nextStateFile = Files.createTempFile("pebbles-next", ".txt")

    println(stateFile)
    println(nextStateFile)

    repeat(75) { i ->
        println("Iteration $i")
        nextStateFile.bufferedWriter().use { nextStateFileWriter ->
            for (n in readNumbers(stateFile)) {
                check(n >= 0)
                val numOfDigitsInBase10 = n.numOfDigitsInBase10
                when {
                    n == 0L -> {
                        nextStateFileWriter.appendLine('1')
                    }

                    numOfDigitsInBase10 % 2 == 0 -> {
                        // Do this with number types instead of Strings just for fun
                        val moveRightDivisor = 10.0.pow(numOfDigitsInBase10 / 2).toLong()
                        val leftHalf = n / moveRightDivisor
                        val rightHalf = n - leftHalf * moveRightDivisor
                        nextStateFileWriter.appendLine(leftHalf.toString())
                        nextStateFileWriter.appendLine(rightHalf.toString())
                    }

                    else -> {
                        nextStateFileWriter.appendLine((n * 2024).toString())
                    }
                }
            }
        }
        Files.move(nextStateFile, stateFile, StandardCopyOption.REPLACE_EXISTING)
    }
    println("Counting numbers...")
    println(readNumbers(stateFile).count())
}

private fun readNumbers(path: Path): Sequence<Long> =
    sequence {
        path.forEachLine { line ->
            yield(line.toLong())
        }
    }

//private val Long.numOfDigitsInBase10 get() = log10(this.toDouble()).toInt() + 1
private val Long.numOfDigitsInBase10 get() = toString().length