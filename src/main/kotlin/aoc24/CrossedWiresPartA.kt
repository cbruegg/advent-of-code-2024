package aoc24

fun main() {
    val input = """
        x00: 1
        x01: 1
        x02: 1
        y00: 0
        y01: 1
        y02: 0

        x00 AND y00 -> z00
        x01 XOR y01 -> z01
        x02 OR y02 -> z02
    """.trimIndent()

    val (initialValuesStr, gatesStr) = input.split("\n\n")
    val wireToInitialValue = initialValuesStr.lines()
        .map { it.split(": ") }
        .associate { (wire, iv) -> wire to iv.toInt() }
    val gates = gatesStr.lines()
        .map { it.split(' ') }
        .map { (inputA, type, inputB, _, output) -> Gate(inputA, inputB, output, GateType.valueOf(type)) }

    var wireToValue = wireToInitialValue
    repeat(1) {
        val nextWireToValue = wireToValue.toMutableMap()
        for (gate in gates) {
            nextWireToValue[gate.output] = gate.evaluate(wireToValue)
        }
        wireToValue = nextWireToValue
    }

    val zEntries = wireToValue.filterKeys { it.startsWith("z") }
        .entries
        .sortedBy { it.key }
    zEntries.forEach { (wire, value) -> println("$wire: $value") }

    var decimal = 0
    for ((i, entry) in zEntries.withIndex()) {
        val (_, value) = entry
        decimal = decimal or (value shl i)
    }

    println("decimal=$decimal")
}

enum class GateType { AND, XOR, OR }

data class Gate(val inputA: String, val inputB: String, val output: String, val type: GateType)

fun Gate.evaluate(wireToValue: Map<String, Int>): Int =
    when (type) {
        GateType.AND -> wireToValue.getValue(inputA) and wireToValue.getValue(inputB)
        GateType.XOR -> wireToValue.getValue(inputA) xor wireToValue.getValue(inputB)
        GateType.OR -> wireToValue.getValue(inputA) or wireToValue.getValue(inputB)
    }