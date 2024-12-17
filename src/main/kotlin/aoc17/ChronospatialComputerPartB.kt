package aoc17

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

fun main() {
    val input = File("inputs/aoc17/input.txt").readLines()

//    val input = """
//        Register A: 2024
//        Register B: 0
//        Register C: 0
//
//        Program: 0,3,5,4,3,0
//    """.trimIndent().lines()

    val b = input[1].substringAfter(": ").toLong()
    val c = input[2].substringAfter(": ").toLong()
    val program = input[4].substringAfter(": ").split(',').map { it.toInt() }.toIntArray()

//    var outIndex = 0
//    val outFunc = { out: Int ->
//        if (outIndex in program.indices && program[outIndex] == out) {
//            outIndex++
//            true
//        } else {
//            false
//        }
//    }
//    runComputer(0b000, b, c, program, outFunc)
//    println(outIndex)
//    outIndex = 0
//    runComputer(0b000000, b, c, program, outFunc)
//    println(outIndex)
//    outIndex = 0
//    runComputer(0b011000000, b, c, program, outFunc)
//    println(outIndex)
//    outIndex = 0
//    runComputer(0b101011000000, b, c, program, outFunc)
//    println(outIndex)
//    outIndex = 0
//    runComputer(0b100101011000000, b, c, program, outFunc)
//    println(outIndex)
//    outIndex = 0
//    runComputer(0b011100101011000000, b, c, program, outFunc)
//    println(outIndex)
//    outIndex = 0

    val a = dfs(b, c, program)
    println("a=$a")
    return

    var goodAPrefix = 0L
    var goodOutputs = 0
    var aDelta = 0L
    fun a() = goodAPrefix + (aDelta shl (goodOutputs * 3)) // 3 because my program iterates over A in chunks of 3 bits
    while (a() in 0..Long.MAX_VALUE) {
        val a = a()
        // 3 because my program iterates over A in chunks of 3 bits
//        if (!a.hasBitPrefix(goodAPrefix, goodOutputs * 3)) {
//            continue
//        }

        if (a.mod(10000000) == 0) {
            println("Progress: ${((100.0 * a) / Long.MAX_VALUE).roundToInt()} %")
        }

        var outIndex = 0
        var firstOut = true
        runComputer(startA = a, startB = b, startC = c, program = program) { outValue ->
            if (program[outIndex] != outValue) {
//              Output is not identical to original program
                false
            } else {
                outIndex++
                if (outIndex > goodOutputs) {
                    goodOutputs = outIndex
                    // 3 because my program iterates over A in chunks of 3 bits
//                    goodAPrefix = a and ((1L.shl(goodOutputs * 3) - 1))
                    goodAPrefix = a
                    aDelta = 0
                    println("prefix=${goodAPrefix.toString(2)}")
                    println("goodOutputs=$goodOutputs")
                }
                true
            }
//            if (!firstOut) {
//                print(',')
//            }
//            print(outValue)
//            firstOut = false
//            true
        }

//        if (outIndex > 6) {
//            println("startA = ${a.toString(2)}, correctDigits = $outIndex")
//        }

        if (outIndex == program.size) {
            println("Start value startA=$a lets the program output itself!")
            return
        }

        aDelta++
    }
    val finalA = a()
    println(finalA)
}

fun d(pref: Long = 0, len: Int = 0) {
    if (len > 6) return

    for (suf in 0..<(2 shl 2)) {
        val a = pref + (suf shl len)
        println(a.toString(2).padStart(len, padChar = '0').padStart(15))
        d(a, len + 3)
    }
}

fun dfs(
    b: Long,
    c: Long,
    program: IntArray,
    goodAPrefix: Long = 0,
    goodAPrefixLength: Int = 0,
    treeRecord: Int = 0
): Long {
    // We need to produce 16 octal numbers for the challenge
    // One octal number has 3 bits
    // So we need to build numbers of 16 * 3 bits = 48 bits
    for (prefixA in 0..<(2L shl 2)) {
        val a = (prefixA shl (48 - 3 - goodAPrefixLength)) + goodAPrefix
        println("Trying ${a.toString(2).padStart(48, '0')}")
        var outIndex = 0
        val relevantIndex = (16 - 1) - (goodAPrefixLength / 3)
        var match = false
        runComputer(a, b, c, program) { out ->
            if (outIndex++ == relevantIndex && program[relevantIndex] == out) {
                match = true
            }
            true
        }
        if (record.getAndUpdate { if (it < outIndex) outIndex else it } < outIndex) {
            println("Tried a=${a.toString(2)} = [$prefixA][${goodAPrefix.toString(8)}], got $outIndex correct digits")
        }
        if (relevantIndex == 0 && match) {
            return a
        }
        if (match) {
            val fullA = dfs(b, c, program, goodAPrefix = a, goodAPrefixLength = goodAPrefixLength + 3, outIndex)
            if (fullA != -1L) {
                return fullA
            }
        }
    }

    return -1
}

val record = AtomicInteger(0)
fun dfs2(
    b: Long,
    c: Long,
    program: IntArray,
    goodAPrefix: Long = 0,
    goodAPrefixLength: Int = 0,
    treeRecord: Int = 0
): Long {
//    d()
    if (goodAPrefixLength > 62) return -1

//    // combine lower bits with upper 3 bits because my program iterates over A in chunks of 3 bits
//    val candidates = (0..<(2L shl 2)).map { goodAPrefix + (it shl goodAPrefixLength) }
//    val candidateToCorrectGuesses = candidates.map { a ->
//        a to run {
//            var outIndex = 0
//            runComputer(a, b, c, program) { out ->
//                if (outIndex in program.indices && program[outIndex] == out) {
//                    outIndex++
//                    true
//                } else {
//                    false
//                }
//            }
//            outIndex
//        }
//    }
//    for ((a, correctGuesses) in candidateToCorrectGuesses.sortedByDescending { it.second }) {
//        if (false || record.getAndUpdate { if (it < correctGuesses) correctGuesses else it } < correctGuesses) {
//            println("Tried a=${a.toString(2)}, got $correctGuesses correct digits")
//        }
//        if (correctGuesses == program.size) {
//            return a
//        }
//        if (correctGuesses >= treeRecord) {
//            val fullA = dfs(b, c, program, goodAPrefix = a, goodAPrefixLength = goodAPrefixLength + 3, correctGuesses)
//            if (fullA != -1L) {
//                return fullA
//            }
//        }
//    }
//    return -1

    for (prefixA in 0..<(2L shl 2)) {
        val a = goodAPrefix + (prefixA shl goodAPrefixLength)
//        val a = (goodAPrefix shl 3) + (prefixA)
        var outIndex = 0
        runComputer(a, b, c, program) { out ->
            if (outIndex in program.indices && program[outIndex] == out) {
                outIndex++
                true
            } else {
                false
            }
        }
        if (record.getAndUpdate { if (it < outIndex) outIndex else it } < outIndex) {
            println("Tried a=${a.toString(2)} = [$prefixA][${goodAPrefix.toString(8)}], got $outIndex correct digits")
        }
        if (outIndex == program.size) {
            return a
        }
        if (outIndex >= treeRecord) {
            val fullA = dfs(b, c, program, goodAPrefix = a, goodAPrefixLength = goodAPrefixLength + 3, outIndex)
            if (fullA != -1L) {
                return fullA
            }
        }
    }
    return -1
}

/**
 * prefix in least-significant-bit order
 */
fun Long.hasBitPrefix(prefix: Long, prefixLength: Int): Boolean {
    // Step 1: Create an n-bit mask with the least significant n bits set to 1
    val mask = (1L shl prefixLength) - 1 // e.g., for n = 3, mask = 0b111

    // Step 2: Compare the n-bit prefix of `another` with `prefix`
    return (this and mask) == (prefix and mask)
}