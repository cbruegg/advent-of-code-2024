package aoc17

import java.io.File

fun main() {
    val input = File("inputs/aoc17/input.txt").readLines()
    // Disassembled:
    // B <- A mod 8
    // B <- B xor 7
    // C <- A >> B
    // B <- B xor C
    // B <- B xor 4
    // OUT <- B mod 8
    // A <- A >> 3
    // IF A != 0 THEN JMP 0

    // Note that the example input will not work without adaptions to the dfs function anymore
    // as we've hardcoded a target of 16 hash values
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

    val a = dfs(b, c, program)
    println("a=$a")
}

fun dfs(
    b: Long,
    c: Long,
    program: IntArray,
    goodAPrefix: Long = 0,
    goodAPrefixLength: Int = 0
): Long {
    // We need to produce 16 octal numbers (= 8 instructions + 8 operands) for the challenge.
    // One octal number has 3 bits (which the program shifts the A register by in every iteration).
    // So we need to build numbers of 16 * 3 bits = 48 bits.
    for (prefixA in 0..<(2L shl 2)) {
        val a = (prefixA shl (48 - 3 - goodAPrefixLength)) + goodAPrefix
        println("Trying ${a.toString(2).padStart(48, '0')}")
        // As we're reversing a hash function, let's brute-force the values it emitted from last to first,
        // basically executing the hash function in reverse order.
        val relevantIndex = (16 - 1) - (goodAPrefixLength / 3)
        var outIndex = 0
        var match = false
        runComputer(a, b, c, program) { out ->
            if (outIndex++ == relevantIndex && program[relevantIndex] == out) {
                match = true
            }
            true
        }
        if (relevantIndex == 0 && match) {
            return a
        }
        if (match) {
            val fullA = dfs(b, c, program, goodAPrefix = a, goodAPrefixLength = goodAPrefixLength + 3)
            if (fullA != -1L) {
                return fullA
            }
        }
    }

    return -1
}
