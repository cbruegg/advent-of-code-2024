package aoc21

import java.io.File
import kotlin.math.min

fun main() {
    val codes = File("inputs/aoc21/input.txt").readLines()
    var totalComplexity = 0
    for (code in codes) {
        println(code)

        val allDirections = numericKeypad.computeDirections(code)
        var length = Int.MAX_VALUE
        for (rawDirections in allDirections) {
            // TODO Can we reuse the shortestKeypadRoute function here?
            val directions = "A$rawDirections"
            var routeLen = 0
            for (i in  0..<directions.lastIndex) {
                val a = directions[i]
                val b = directions[i + 1]
                val lenAToB = directionalKeypad.shortestKeypadRoute(KeypadButton(a), KeypadButton(b), levels = 1)
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

fun Keypad.computeDirections(code: String, pos: KeypadButton = KeypadButton('A')): List<String> {
    if (code.isEmpty()) return listOf("")

    val next = code[0]
    val routesToNext = keypadRoute(pos, KeypadButton(next))
    val fullRoutes = mutableListOf<String>()
    for (routeToNext in routesToNext) {
        for (restOfRoute in computeDirections(code.substring(1), pos = KeypadButton(next))) {
            fullRoutes += routeToNext + 'A' + restOfRoute
        }
    }

    return fullRoutes
}

fun applyNumericKeypadRoute(route: String): String {
    val keypad = arrayOf(
        arrayOf('7', '8', '9'),
        arrayOf('4', '5', '6'),
        arrayOf('1', '2', '3'),
        arrayOf(' ', '0', 'A'),
    )
    val sb = StringBuilder()
    validateKeypadRoute(keypadLayout = keypad, startX = 2, startY = 3, route = route) { i, button ->
        sb.append(button)
    }
    return sb.toString()
}

fun applyDirectionalKeypadRoute(route: String): String {
    val keypad = arrayOf(
        arrayOf(' ', '^', 'A'),
        arrayOf('<', 'v', '>'),
    )
    val sb = StringBuilder()
    validateKeypadRoute(keypadLayout = keypad, startX = 2, startY = 0, route = route) { i, button ->
        sb.append(button)
    }
    return sb.toString()
}

fun validateNumericKeypadRoute(route: String, code: String) {
    val keypad = arrayOf(
        arrayOf('7', '8', '9'),
        arrayOf('4', '5', '6'),
        arrayOf('1', '2', '3'),
        arrayOf(' ', '0', 'A'),
    )
    validateKeypadRoute(keypadLayout = keypad, startX = 2, startY = 3, route = route) { i, button ->
        check(code[i] == button) {
            "Route $route is invalid for code $code at i=$i. Expected ${code[i]}, but pressed $button"
        }
    }
}

fun validateDirectionalKeypadRoute(route: String, code: String) {
    val keypad = arrayOf(
        arrayOf(' ', '^', 'A'),
        arrayOf('<', 'v', '>'),
    )
    validateKeypadRoute(keypadLayout = keypad, startX = 2, startY = 0, route = route) { i, button ->
        check(code[i] == button) {
            "Route $route is invalid for code $code at i=$i. Expected ${code[i]}, but pressed $button"
        }
    }
}

fun validateKeypadRoute(
    keypadLayout: Array<Array<Char>>,
    startX: Int,
    startY: Int,
    route: String,
    validator: (index: Int, button: Char) -> Unit
) {
    var x = startX
    var y = startY
    var i = 0

    fun checkBounds(action: Char, x: Int, y: Int) {
        check(y in keypadLayout.indices) { "Ran action '$action', but moved out of bounds at ($x, $y) [y]" }
        check(x in keypadLayout[y].indices) { "Ran action '$action', but moved out of bounds at ($x, $y) [x]" }
    }

    for (action in route) {
        when (action) {
            '>' -> {
                x++
                checkBounds(action, x, y)
            }

            '<' -> {
                x--
                checkBounds(action, x, y)
            }

            '^' -> {
                y--
                checkBounds(action, x, y)
            }

            'v' -> {
                y++
                checkBounds(action, x, y)
            }

            'A' -> {
                val button = keypadLayout[y][x]
                validator(i, button)
                i++
            }
        }
    }
}

fun formatCode(code: String) = code.replace("A", "A ")