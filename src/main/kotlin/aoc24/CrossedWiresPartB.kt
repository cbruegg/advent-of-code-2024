package aoc24

fun main() {
    val input = """
        x00: 0
        x01: 1
        x02: 0
        x03: 1
        x04: 0
        x05: 1
        y00: 0
        y01: 0
        y02: 1
        y03: 1
        y04: 0
        y05: 1

        x00 AND y00 -> z05
        x01 AND y01 -> z02
        x02 AND y02 -> z01
        x03 AND y03 -> z03
        x04 AND y04 -> z04
        x05 AND y05 -> z00
    """.trimIndent()

    val (initialValuesStr, gatesStr) = input.split("\n\n")
    val wireToInitialValue = initialValuesStr.lines()
        .map { it.split(": ") }
        .associate { (wire, iv) -> wire to iv.toInt() }
    val gates = gatesStr.lines()
        .map { it.split(' ') }
        .map { (inputA, type, inputB, _, output) -> Gate(inputA, inputB, output, GateType.valueOf(type)) }

    val x = collectBits(wireToInitialValue, "x")
    val y = collectBits(wireToInitialValue, "y")
    val expectedZ = x and y

    val wireToValue = runCircuit(wireToInitialValue, gates)

    val zEntries = wireToValue.filterKeys { it.startsWith("z") }
        .entries
        .sortedBy { it.key }
    zEntries.forEach { (wire, value) -> println("$wire: $value") }

    val z = collectBits(wireToValue)

    println("x=$x, y=$y, z=$z, expectedZ=$expectedZ")
}
