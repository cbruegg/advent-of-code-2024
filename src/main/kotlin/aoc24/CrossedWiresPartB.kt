package aoc24

import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import kotlin.math.min
import kotlin.random.Random

// WARNING: My code here claims that my solution is wrong, even though it's correct.
// I found the solution in CrossedWiresPartBAttempt2:
//     val swappedGates = setOf(
//        Gate("ksn", "nft", "jpj", GateType.XOR) to Gate("ksn", "nft", "z12", GateType.AND),
//        Gate("x26", "y26", "z26", GateType.AND) to Gate("bvp", "gdb", "kgj", GateType.XOR),
//        Gate("x34", "y34", "vvw", GateType.XOR) to Gate("y34", "x34", "chv", GateType.AND),
//        Gate("cds", "rkv", "rts", GateType.XOR) to Gate("sdj", "nph", "z07", GateType.OR) // looks good
//    )

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

    val (initialValuesStr, gatesStr) = input.split("\n\n")
    val wireToInitialValue = initialValuesStr.lines()
        .map { it.split(": ") }
        .associate { (wire, iv) -> wire to iv.toInt() }
    val gates = gatesStr.lines()
        .map { it.split(' ') }
        .map { (inputA, type, inputB, _, output) -> Gate(inputA, inputB, output, GateType.valueOf(type)) }
        .toSet()

    val swappedGates = setOf(
        Gate("ksn", "nft", "jpj", GateType.XOR) to Gate("ksn", "nft", "z12", GateType.AND),
        Gate("x26", "y26", "z26", GateType.AND) to Gate("bvp", "gdb", "kgj", GateType.XOR),
        Gate("x34", "y34", "vvw", GateType.XOR) to Gate("y34", "x34", "chv", GateType.AND),
        Gate("cds", "rkv", "rts", GateType.XOR) to Gate("sdj", "nph", "z07", GateType.OR) // looks good
    )
    val output = swappedGates.flatMap { listOf(it.first.output, it.second.output) }.sorted().joinToString(separator = ",")
    println("output=$output")

    val fixedGates = gates.map { gate -> fixGate(swappedGates, gate) }.toSet()
    fixedGates.toGraph().validateRippleCarry()
    println(findBadGates(fixedGates))
    val xxx = fixedGates - gates
    println(xxx)
    val remainingGateSwaps = cartesianProduct(fixedGates, fixedGates).toMutableSet()
    while (remainingGateSwaps.size > 1) {
        var badSwapPair: Pair<Gate, Gate>? = null
        for (swapPair in remainingGateSwaps) {
            val (gateA, gateB) = swapPair
            val fullFixedGates = fixedGates.map { gate -> fixGate(setOf(Pair(gateA, gateB)), gate) }.toSet()
            val badGates = withTimeoutOrNull(1_000) { findBadGates(fullFixedGates, repetitions = 1) }
            if (badGates == null) {
                println("Timeout when swapping $gateA with $gateB")
                badSwapPair = swapPair
                break
            }
            if (badGates.isNotEmpty()) {
                println("Still have bad gates when swapping $gateA with $gateB")
                badSwapPair = swapPair
                break
            }
        }
        if (badSwapPair != null) {
            remainingGateSwaps -= badSwapPair
            println("Remaining swap pairs: ${remainingGateSwaps.size}")
        }
    }
    println("remainingGateSwaps=$remainingGateSwaps")
    val stillBad = findBadGates(fixedGates.map { gate -> fixGate(remainingGateSwaps, gate) }.toSet())
    println("stillBad=$stillBad")
    return

    for (gateA in fixedGates) {
        println("gateA=$gateA")
        for (gateB in fixedGates) {
            val fullSwappedGates = swappedGates + Pair(gateA, gateB)
            val fullFixedGates = fixedGates.map { gate -> fixGate(fullSwappedGates, gate) }.toSet()
            if (findBadGates(fullFixedGates).isEmpty()) {
                println("No bad gates when swapping $gateA with $gateB")
            }
        }
    }

//    println(generateMermaidDiagram(wireToInitialValue.keys, gates))
    findBadGates(fixedGates)

    return

    val x = collectBits(wireToInitialValue, "x")
    val y = collectBits(wireToInitialValue, "y")
    val expectedZ = x and y

    val wireToValue = runCircuit(wireToInitialValue, gates)

    val zEntries = wireToValue.filterKeys { it.startsWith("z") }
        .entries
        .sortedBy { it.key }
//    zEntries.forEach { (wire, value) -> println("$wire: $value") }

    val z = collectBits(wireToValue)

    val badGates = findBadGates(wireToValue, gates, expectedZ)
    println("badGates=$badGates")

    println("x=$x, y=$y, z=$z, expectedZ=$expectedZ")
}

private fun <A, B> cartesianProduct(
    listA: Iterable<A>,
    listB: Iterable<B>,
    distinct: Boolean = true
): Sequence<Pair<A, B>> =
    sequence {
        listA.forEach { a ->
            listB.forEach { b ->
                if (!distinct || a != b) {
                    yield(a to b)
                }
            }
        }
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
        // TODO Verify that x00 is least significant bit
        val initialX = Random.nextLong(0b11111111111111111111111111111111111111111111L)
        val initialY = Random.nextLong(0b11111111111111111111111111111111111111111111L)
        val expectedZ = initialX + initialY
        val wireToInitialValue =
            initialX.toString(radix = 2).reversed().mapIndexed { i, bit -> "x${i.toString().padStart(2, '0')}" to bit.digitToInt()}.toMap() +
                    initialY.toString(radix = 2).reversed().mapIndexed { i, bit -> "y${i.toString().padStart(2, '0')}" to bit.digitToInt()}.toMap()
        val wireToValue = runCircuit(wireToInitialValue, gates)
        val actualZ = collectBits(wireToValue)

        val commonSuffix = expectedZ.toString(2).commonSuffixWith(actualZ.toString(2))
        minCommonSuffix = min(minCommonSuffix, commonSuffix.length)

        val zEntries = wireToValue.filterKeys { it.startsWith("z") }
            .entries
            .sortedBy { it.key }
//        zEntries.forEach { (wire, value) -> println("$wire: $value") }

        badGates += findBadGates(wireToValue, gates, expectedZ)
    }
//    println(badGates)
    return badGates
}

fun generateMermaidDiagram(initialValueWires: Set<String>, gates: Set<Gate>): String {
    val sb = StringBuilder("flowchart TD\n")

    fun Gate.nodeName() = "$inputA$type$inputB[$type]"

    for (gate in gates) {
        val (inputA, inputB, output, type) = gate

        val nodeName = gate.nodeName()
        for (input in listOf(inputA, inputB)) {
            if (input in initialValueWires) {
                sb.appendLine("    $input{$input} --> $nodeName")
            } else {
                sb.appendLine("    $input($input) --> $nodeName")
            }
        }
        sb.appendLine("    $nodeName --> $output{$output}")
    }
    return sb.toString()
}


fun generateMermaidDiagram2(initialValueWires: Set<String>, gates: Set<Gate>): String {
    val sb = StringBuilder("flowchart TD\n")
//    for (initialValueWire in initialValueWires) {
//
//    }

    fun Gate.nodeName() = "$inputA$type$inputB[$type]"
//    fun Gate.nodeName() = "$inputA$type$inputB[$inputA $type $inputB]"

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

    fun findAllInputGates(gate: Gate, gates: Set<Gate>): Set<Gate> {
        val directInputGates = gates.filter { it.output == gate.inputA || it.output == gate.inputB }.toSet()
        return directInputGates
        val transitiveInputGates = directInputGates.flatMap { findAllInputGates(it, gates) }.toSet()
        return directInputGates + transitiveInputGates
    }

    val badGates = gates.filter { it.output in badWires }.toSet()
    return badGates + badGates.flatMap { findAllInputGates(it, gates) }
}
