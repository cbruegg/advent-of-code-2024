package aoc24

fun main() {
    val input = """
        x00: 1
        x01: 0
        x02: 1
        x03: 1
        x04: 0
        y00: 1
        y01: 1
        y02: 1
        y03: 1
        y04: 1

        ntg XOR fgs -> mjb
        y02 OR x01 -> tnw
        kwq OR kpj -> z05
        x00 OR x03 -> fst
        tgd XOR rvg -> z01
        vdt OR tnw -> bfw
        bfw AND frj -> z10
        ffh OR nrd -> bqk
        y00 AND y03 -> djm
        y03 OR y00 -> psh
        bqk OR frj -> z08
        tnw OR fst -> frj
        gnj AND tgd -> z11
        bfw XOR mjb -> z00
        x03 OR x00 -> vdt
        gnj AND wpb -> z02
        x04 AND y00 -> kjc
        djm OR pbm -> qhw
        nrd AND vdt -> hwm
        kjc AND fst -> rvg
        y04 OR y02 -> fgs
        y01 AND x02 -> pbm
        ntg OR kjc -> kwq
        psh XOR fgs -> tgd
        qhw XOR tgd -> z09
        pbm OR djm -> kpj
        x03 XOR y03 -> ffh
        x00 XOR y04 -> ntg
        bfw OR bqk -> z06
        nrd XOR fgs -> wpb
        frj XOR qhw -> z04
        bqk OR frj -> z07
        y03 OR x01 -> nrd
        hwm AND bqk -> z03
        tgd XOR rvg -> z12
        tnw OR pbm -> gnj
    """.trimIndent()

    val (initialValuesStr, gatesStr) = input.split("\n\n")
    val wireToInitialValue = initialValuesStr.lines()
        .map { it.split(": ") }
        .associate { (wire, iv) -> wire to iv.toInt() }
    val gates = gatesStr.lines()
        .map { it.split(' ') }
        .map { (inputA, type, inputB, _, output) -> Gate(inputA, inputB, output, GateType.valueOf(type)) }

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
        }
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
    type.evaluate(wireToValue.getValue(inputA), wireToValue.getValue(inputB))

fun GateType.evaluate(inputA: Int, inputB: Int): Int =
    when (this) {
        GateType.AND -> inputA and inputB
        GateType.XOR -> inputA xor inputB
        GateType.OR -> inputA or inputB
    }