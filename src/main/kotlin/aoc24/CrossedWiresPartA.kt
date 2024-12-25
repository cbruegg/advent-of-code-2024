package aoc24

import java.io.File

suspend fun main() {
    val input = File("inputs/aoc24/input.txt").readText()

    val (initialValuesStr, gatesStr) = input.split("\n\n")
    val wireToInitialValue = initialValuesStr.lines()
        .map { it.split(": ") }
        .associate { (wire, iv) -> wire to iv.toInt() }
    val gates = gatesStr.lines()
        .map { it.split(' ') }
        .map { (inputA, type, inputB, _, output) -> Gate(inputA, inputB, output, GateType.valueOf(type)) }

    val wireToValue = runCircuit(wireToInitialValue, gates)

    val zEntries = wireToValue.filterKeys { it.startsWith("z") }
        .entries
        .sortedBy { it.key }
    zEntries.forEach { (wire, value) -> println("$wire: $value") }

    val decimal = collectBits(wireToValue)

    println("decimal=$decimal")
}
