package aoc24

import java.io.File
import kotlin.math.min
import kotlin.random.Random

// This file runs the circuit with random numbers to validate it

suspend fun main() {
//    val input = """
//        x00: 0
//        x01: 1
//        x02: 0
//        x03: 1
//        x04: 0
//        x05: 1
//        y00: 0
//        y01: 0
//        y02: 1
//        y03: 1
//        y04: 0
//        y05: 1
//
//        x00 AND y00 -> z05
//        x01 AND y01 -> z02
//        x02 AND y02 -> z01
//        x03 AND y03 -> z03
//        x04 AND y04 -> z04
//        x05 AND y05 -> z00
//    """.trimIndent()
    val input = File("inputs/aoc24/input.txt").readText()

    val (_, gatesStr) = input.split("\n\n")
    val gates = gatesStr.lines()
        .map { it.split(' ') }
        .map { (inputA, type, inputB, _, output) -> Gate(inputA, inputB, output, GateType.valueOf(type)) }
        .toSet()

    val swappedGates = setOf(
        Gate("ksn", "nft", "jpj", GateType.XOR) to Gate("ksn", "nft", "z12", GateType.AND),
        Gate("x26", "y26", "z26", GateType.AND) to Gate("bvp", "gdb", "kgj", GateType.XOR),
        Gate("x34", "y34", "vvw", GateType.XOR) to Gate("y34", "x34", "chv", GateType.AND),
        Gate("cds", "rkv", "rts", GateType.XOR) to Gate("sdj", "nph", "z07", GateType.OR)
    )
    val output = swappedGates.flatMap { listOf(it.first.output, it.second.output) }.sorted().joinToString(separator = ",")
    println("output=$output")

    val fixedGates = gates.map { gate -> fixGate(swappedGates, gate) }.toSet()
    println(findBadGates(fixedGates))
}

private fun fixGate(
    swappedGates: Set<Pair<Gate, Gate>>,
    gate: Gate
): Gate {
    val swapPair = swappedGates.firstOrNull { it.first == gate || it.second == gate }
    return if (swapPair != null) {
        val (first, second) = swapPair
        val other = if (gate == first) second else first
        gate.copy(output = other.output)
    } else {
        gate
    }
}

suspend fun findBadGates(gates: Set<Gate>, repetitions: Int = 100000): Set<Gate> {
    val badGates = mutableSetOf<Gate>()
    var minCommonSuffix = Int.MAX_VALUE
    repeat(repetitions) {
        val initialX = Random.nextLong(0b11111111111111111111111111111111111111111111L)
        val initialY = Random.nextLong(0b11111111111111111111111111111111111111111111L)
        val expectedZ = initialX + initialY
        val wireToInitialValue = (0..44).flatMap { i ->
            setOf(
                Pair("x${i.toString().padStart(2, '0')}", (initialX shr i).toInt() and 0b1),
                Pair("y${i.toString().padStart(2, '0')}", (initialY shr i).toInt() and 0b1)
            )
        }.toMap()
        val wireToValue = runCircuit(wireToInitialValue, gates)
        val actualZ = collectBits(wireToValue)

        val commonSuffix = expectedZ.toString(2).commonSuffixWith(actualZ.toString(2))
        minCommonSuffix = min(minCommonSuffix, commonSuffix.length)

        badGates += findBadGates(wireToValue, gates, expectedZ)
    }
    return badGates
}

fun generateMermaidDiagram(initialValueWires: Set<String>, gates: Set<Gate>): String {
    val sb = StringBuilder("flowchart TD\n")

    fun Gate.nodeName() = "$inputA$type$inputB[$type]"

    val wiresUsedAsInputs = gates.flatMap { setOf(it.inputA, it.inputB) }.toSet()

    for (gate in gates) {
        val (inputA, inputB, output, type) = gate
        val nodeName = gate.nodeName()
        for (input in listOf(inputA, inputB)) {
            if (inputA in initialValueWires) {
                sb.appendLine("    $input($input) --> $nodeName")
            } else {
                val prevGate = gates.single { it.output == input }
                sb.appendLine("    ${prevGate.nodeName()} --> $nodeName")
            }
        }
        if (output !in wiresUsedAsInputs) {
            // This is a final output gate
            sb.appendLine("    $nodeName --> $output{$output}")
        }
    }
    return sb.toString()
}

fun findBadGates(wireToValue: Map<String, Int>, gates: Set<Gate>, expectedZ: Long): Set<Gate> {
    val zDigitLength = wireToValue.keys.filter { it.startsWith('z') }.maxOf { it.length } - 1
    val expectedWireToValue = expectedZ.toString(radix = 2)
        .reversed()
        .mapIndexed { i, bit -> ("z" + "$i".padStart(zDigitLength, padChar = '0')) to bit.digitToInt() }
        .toMap()
    val badWires = expectedWireToValue.filter { (wire, expectedValue) -> wireToValue[wire] != expectedValue }.keys

    val badGates = gates.filter { it.output in badWires }.toSet()
    return badGates
}
