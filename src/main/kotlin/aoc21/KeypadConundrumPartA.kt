package aoc21

import aoc16.AllShortestPathsResult
import java.io.File
import java.nio.file.Files

fun main() {
    println("Test for 179A:")
    println(formatCode(applyNumericKeypadRoute(applyDirectionalKeypadRoute(applyDirectionalKeypadRoute("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")))))
    println(formatCode(applyDirectionalKeypadRoute(applyDirectionalKeypadRoute("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"))))
    println(formatCode(applyDirectionalKeypadRoute("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")))
    println(formatCode("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"))
    println()

    val codes = File("inputs/aoc21/input.txt").readLines()
    var totalComplexity = 0
    for (code in codes) {
        println(code)
        var shortestDirectionalKeypad2Directions: String? = null
        for (numericKeypadDirections in numericKeypad.computeDirections(code)) {
//            println(formatCode(numericKeypadDirections))
            validateNumericKeypadRoute(numericKeypadDirections, code)

            for (directionalKeypad1Directions in directionalKeypad.computeDirections(numericKeypadDirections)) {
//                println(formatCode(directionalKeypad1Directions))
                validateDirectionalKeypadRoute(directionalKeypad1Directions, numericKeypadDirections)

                for (directionalKeypad2Directions in directionalKeypad.computeDirections(directionalKeypad1Directions)) {
                    if (shortestDirectionalKeypad2Directions == null || shortestDirectionalKeypad2Directions.length > directionalKeypad2Directions.length) {
                        shortestDirectionalKeypad2Directions = directionalKeypad2Directions
                    }
//                    println(formatCode(directionalKeypad2Directions))
                    validateDirectionalKeypadRoute(directionalKeypad2Directions, directionalKeypad1Directions)
                }
            }
        }

        check(shortestDirectionalKeypad2Directions != null) { "No route found" }

        val length = shortestDirectionalKeypad2Directions.length
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