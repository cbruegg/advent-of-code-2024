package aoc17

import kotlin.math.pow

const val OP_ADV = 0
const val OP_BXL = 1
const val OP_BST = 2
const val OP_JNZ = 3
const val OP_BXC = 4
const val OP_OUT = 5
const val OP_BDV = 6
const val OP_CDV = 7

fun runComputer(startA: Long, startB: Long, startC: Long, program: IntArray, output: (Int) -> Boolean) {
    // CPU state
    var a = startA
    var b = startB
    var c = startC
    var instructionPointer = 0

    fun evalComboOperand(operand: Int): Long =
        when (operand) {
            0, 1, 2, 3 -> operand.toLong()
            4 -> a
            5 -> b
            6 -> c
            else -> error("Unexpected operand $operand")
        }

    while (instructionPointer in program.indices) {
        check(a >= 0)
        check(b >= 0)
        check(c >= 0)
        val instruction = program[instructionPointer]
        val operand = program[instructionPointer + 1]
        when (instruction) {
            OP_ADV -> {
                a = a shr evalComboOperand(operand).toIntOrThrow()
            }

            OP_BXL -> {
                b = b xor operand.toLong()
            }

            OP_BST -> {
                b = evalComboOperand(operand).rem(8)
            }

            OP_JNZ -> {
                if (a != 0L) {
                    instructionPointer = operand
                } else {
                    instructionPointer += 2
                }
            }

            OP_BXC -> {
                b = b xor c
            }

            OP_OUT -> {
                val shouldContinue = output(evalComboOperand(operand).rem(8).toIntOrThrow())
                if (!shouldContinue) return
            }

            OP_BDV -> {
                b = a shr evalComboOperand(operand).toIntOrThrow()
            }

            OP_CDV -> {
                c = a shr evalComboOperand(operand).toIntOrThrow()
            }

            else -> error("Unknown instruction $instruction")
        }

        if (instruction != OP_JNZ) {
            instructionPointer += 2
        } // else we already did it in OP_JNZ handler
    }
}

fun Long.toIntOrThrow(): Int =
    if (this in Int.MIN_VALUE..Int.MAX_VALUE) toInt() else error("$this does not fit into Int")