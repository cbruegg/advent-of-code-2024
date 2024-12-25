package aoc24

import aoc16.Edge
import aoc16.Graph
import java.io.File

fun main() {
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

    gates.toGraph().validateRippleCarry()
}

sealed interface CircuitNode {
    data class SignalNode(val name: String) : CircuitNode
    data class GateNode(val info: Gate) : CircuitNode
}

fun Set<Gate>.toGraph(): Graph<CircuitNode> {
    val signals = mutableMapOf<String, CircuitNode.SignalNode>()
    val gates = mutableSetOf<CircuitNode.GateNode>()
    val edges = mutableMapOf<CircuitNode, MutableSet<Edge<CircuitNode>>>()
    for (gate in this) {
        val inputANode = signals.getOrPut(gate.inputA) { CircuitNode.SignalNode(gate.inputA) }
        val inputBNode = signals.getOrPut(gate.inputB) { CircuitNode.SignalNode(gate.inputB) }
        val outputNode = signals.getOrPut(gate.output) { CircuitNode.SignalNode(gate.output) }
        val gateNode = CircuitNode.GateNode(gate)
        gates += gateNode
        edges.getOrPut(inputANode, { mutableSetOf() }) += Edge(1, gateNode)
        edges.getOrPut(inputBNode, { mutableSetOf() }) += Edge(1, gateNode)
        edges.getOrPut(gateNode, { mutableSetOf() }) += Edge(1, outputNode)
    }

    return Graph((signals.values + gates).toSet(), edges)
}

fun Graph<CircuitNode>.validateRippleCarry() {
    val xyNodes = nodes.filterIsInstance<CircuitNode.SignalNode>()
        .filter { it.name[0] in setOf('x', 'y') }
        .groupBy { it.name.substring(1) }
        .values
    for (xy in xyNodes) {
        val (xNode, yNode) = xy
        val bitId = xNode.name.removePrefix("x").removePrefix("y")
        try {
            for (xyNode in xy) {
                val outgoing = edges[xyNode]
                check(outgoing != null) { "$xyNode has no outgoing edges" }
                check(outgoing.size == 2) { "$xyNode has != 2 outgoing edges" }

                val and =
                    outgoing.single { it.target is CircuitNode.GateNode && it.target.info.type == GateType.AND }.target
                val xor =
                    outgoing.single { it.target is CircuitNode.GateNode && it.target.info.type == GateType.XOR }.target
                // x and y nodes should be connected to the same AND and XOR gates
                val otherNode = if (xyNode == xNode) yNode else xNode
                check(edges[otherNode]?.map { it.target }?.toSet() == setOf(and, xor)) {
                    "$otherNode should be connected to ${setOf(and, xor)}, like $xyNode"
                }

                val andOutgoing = edges[and]
                check(andOutgoing != null && andOutgoing.size == 1) { "AND gate after $xyNode should have one outgoing edge" }
                val gatesAfterAnd = edges[andOutgoing.first().target]
                check(gatesAfterAnd != null && gatesAfterAnd.size == 1) { "AND gate after $xyNode should output to exactly one value (that forwards to an OR gate)" }
                val orGateAfterAnd = gatesAfterAnd.single().target
                check(orGateAfterAnd is CircuitNode.GateNode && orGateAfterAnd.info.type == GateType.OR) { "AND gate after $xyNode should output to an OR" }

                val xorOutgoing = edges[xor]
                check(xorOutgoing != null && xorOutgoing.size == 1) { "XOR gate after $xyNode should have one outgoing edge" }
                val gatesAfterXor = edges[xorOutgoing.first().target]?.map { it.target }
                check(gatesAfterXor != null && gatesAfterXor.size == 2) { "XOR gate after $xyNode should have two outgoing edges (one to AND, one to XOR)" }
                val andGateAfterXor =
                    gatesAfterXor.map { it as CircuitNode.GateNode }.single { it.info.type == GateType.AND }
                // TODO Verify inputB of andGateAfterXor
                val nodesAfterAndGateAfterXor = edges[andGateAfterXor]
                check(nodesAfterAndGateAfterXor != null && nodesAfterAndGateAfterXor.size == 1) { "AND gate after XOR of $xyNode should have one output" }
                val orGateAfterAndGateAfterXor = edges[nodesAfterAndGateAfterXor.single().target]?.single()?.target
                check(orGateAfterAnd == orGateAfterAndGateAfterXor) { "AND gate after $xyNode should be connected to the same OR gate as the AND gate after XOR" }

                val xorGateAfterXor =
                    gatesAfterXor.map { it as CircuitNode.GateNode }.single { it.info.type == GateType.XOR }
                val zNode = edges[xorGateAfterXor]?.single()?.target as? CircuitNode.SignalNode
                check(zNode?.name == "z$bitId") { "XOR after XOR after $xyNode should output to z$bitId" }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
