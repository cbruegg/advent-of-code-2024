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

fun runComputer(startA: Int, startB: Int, startC: Int, program: List<Int>, output: (Int) -> Unit) {
    // CPU state
    var a = startA
    var b = startB
    var c = startC
    var instructionPointer = 0

    fun evalComboOperand(operand: Int): Int =
        when (operand) {
            0, 1, 2, 3 -> operand
            4 -> a
            5 -> b
            6 -> c
            else -> error("Unexpected operand $operand")
        }

    while (instructionPointer in program.indices) {
        val instruction = program[instructionPointer]
        val operand = program[instructionPointer + 1]
        when (instruction) {
            OP_ADV -> {
                val numerator = a
                val denominator = 2.0.pow(evalComboOperand(operand))
                a = (numerator / denominator).toInt()
            }
            OP_BXL -> {
                b = b xor operand
            }
            OP_BST -> {
                b = evalComboOperand(operand).rem(8)
            }
            OP_JNZ -> {
                if (a != 0) {
                    instructionPointer = operand
                } else {
                    instructionPointer += 2
                }
            }
            OP_BXC -> {
                b = b xor c
            }
            OP_OUT -> {
                output(evalComboOperand(operand).rem(8))
            }
            OP_BDV -> {
                val numerator = a
                val denominator = 2.0.pow(evalComboOperand(operand))
                b = (numerator / denominator).toInt()
            }
            OP_CDV -> {
                val numerator = a
                val denominator = 2.0.pow(evalComboOperand(operand))
                c = (numerator / denominator).toInt()
            }
            else -> error("Unknown instruction $instruction")
        }

        if (instruction != OP_JNZ) {
            instructionPointer += 2
        } // else we already did it in OP_JNZ handler
    }
}