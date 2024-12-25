package aoc24

import kotlinx.coroutines.yield

enum class GateType { AND, XOR, OR }

data class Gate(val inputA: String, val inputB: String, val output: String, val type: GateType)

fun Gate.evaluate(wireToValue: Map<String, Int>): Int =
    type.evaluate(wireToValue.getValue(inputA), wireToValue.getValue(inputB))

fun GateType.evaluate(inputA: Int, inputB: Int): Int =
    when (this) {
        GateType.AND -> inputA and inputB
        GateType.XOR -> inputA xor inputB
        GateType.OR -> inputA or inputB
    }

suspend fun runCircuit(wireToInitialValue: Map<String, Int>, gates: Collection<Gate>): Map<String, Int> {
    val wireToAffectedGates = mutableMapOf<String, MutableSet<Gate>>()
    for (gate in gates) {
        wireToAffectedGates.getOrPut(gate.inputA, { mutableSetOf() }) += gate
        wireToAffectedGates.getOrPut(gate.inputB, { mutableSetOf() }) += gate
    }

    val wireToValue = mutableMapOf<String, Int>()
    val valueProcessingQueue = wireToInitialValue.entries
        .map { (wire, value) -> Pair(wire, value) }
        .toCollection(ArrayDeque())

    while (valueProcessingQueue.isNotEmpty()) {
        val (wire, value) = valueProcessingQueue.removeFirst()
        wireToValue[wire] = value
        val affectedGates = wireToAffectedGates[wire] ?: emptySet()
        for (affectedGate in affectedGates) {
            val a = wireToValue[affectedGate.inputA] ?: continue // wait for missing value
            val b = wireToValue[affectedGate.inputB] ?: continue // wait for missing value
            valueProcessingQueue += affectedGate.output to affectedGate.type.evaluate(a, b)
            yield()
        }
    }

    return wireToValue
}

fun collectBits(wireToValue: Map<String, Int>, prefix: String = "z"): Long {
    val zEntries = wireToValue.filterKeys { it.startsWith(prefix) }
        .entries
        .sortedBy { it.key }

    var decimal = 0L
    for ((i, entry) in zEntries.withIndex()) {
        val (_, value) = entry
        decimal = decimal or (value.toLong() shl i)
    }
    return decimal
}