package aoc21

import java.io.File
import kotlin.math.min

fun main() {
    val codes = File("inputs/aoc21/input.txt").readLines()
    var totalComplexity = 0L
    for (code in codes) {
        println(code)

        val allDirections = numericKeypad.computeDirections(code)
        var length = Long.MAX_VALUE
        for (rawDirections in allDirections) {
            // TODO Can we reuse the shortestKeypadRoute function here?
            val directions = "A$rawDirections"
            var routeLen = 0L
            for (i in  0..<directions.lastIndex) {
                val a = directions[i]
                val b = directions[i + 1]
                val lenAToB = directionalKeypad.shortestKeypadRoute(KeypadButton(a), KeypadButton(b), levels = 25 - 1)
                routeLen += lenAToB
            }
            length = min(length, routeLen)
        }

        val numericPart = Regex("""0*(\d*)A?""").matchEntire(code)!!.groupValues[1].toInt()
        println("length = $length, numericPart = $numericPart")
        val complexity = length * numericPart
        println("complexity = $complexity")
        totalComplexity += complexity
        println()
    }

    println("totalComplexity = $totalComplexity")
}
