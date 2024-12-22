package aoc21

import java.io.File
import kotlin.math.min

fun main() {
    // TODO Precompute all combos? I guess no need, cause we're already caching
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 0))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 1))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 2))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 3))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 10))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 15))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 18))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 20))
    println(directionalKeypad.keypadRoutes3(KeypadButton('A'), KeypadButton('^'), levels = 25))

    println(directionalKeypad.keypadRoutes2(KeypadButton('A'), KeypadButton('^'), levels = 0))
    println(directionalKeypad.keypadRoutes2(KeypadButton('A'), KeypadButton('^'), levels = 1))
    println(directionalKeypad.keypadRoutes2(KeypadButton('A'), KeypadButton('^'), levels = 2))
    println(directionalKeypad.keypadRoutes2(KeypadButton('A'), KeypadButton('^'), levels = 3))
//    println(directionalKeypad.keypadRoutes2(KeypadButton('A'), KeypadButton('^'), levels = 10))

    println(directionalKeypad.keypadRoutes(KeypadButton('A'), KeypadButton('^'), levels = 0))
    println(directionalKeypad.keypadRoutes(KeypadButton('A'), KeypadButton('^'), levels = 1))
    println(directionalKeypad.keypadRoutes(KeypadButton('A'), KeypadButton('^'), levels = 2))
//    println(directionalKeypad.keypadRoutes(KeypadButton('A'), KeypadButton('^'), levels = 3))
//    println(directionalKeypad.keypadRoutes(KeypadButton('A'), KeypadButton('^'), levels = 10))

    println("Test for 179A:")
    println(formatCode(applyNumericKeypadRoute(applyDirectionalKeypadRoute(applyDirectionalKeypadRoute("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")))))
    println(formatCode(applyDirectionalKeypadRoute(applyDirectionalKeypadRoute("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"))))
    println(formatCode(applyDirectionalKeypadRoute("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A")))
    println(formatCode("<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A"))
    println()

    val chain = listOf(
        { code: String -> numericKeypad.computeDirections(code) },
        { code: String -> directionalKeypad.computeDirections(code) },
        { code: String -> directionalKeypad.computeDirections(code) },
    )

    val codes = File("inputs/aoc21/input.txt").readLines()
    var totalComplexity = 0
    for (code in codes) {
        println(code)

        val allDirections = numericKeypad.computeDirections(code)
        var minRoute = Int.MAX_VALUE
        var minRouteStr: String? = null
        for (rawDirections in allDirections) {
            val directions = "A$rawDirections"
            var routeLen = 0
            var route = ""
            for (i in  0..<directions.lastIndex) {
                val a = directions[i]
                val b = directions[i + 1]
                val lenAToB = directionalKeypad.keypadRoutes3(KeypadButton(a), KeypadButton(b), levels = 1)
                routeLen += lenAToB
                // TODO correct?
//                routeLen += directionalKeypad.keypadRoutes3(KeypadButton(b), KeypadButton('A'), levels = 0)
//                routeLen += 1 // Press A

                route += directionalKeypad.keypadRoutes(KeypadButton(a), KeypadButton(b), levels = 1).minBy { it.length }
//                route += directionalKeypad.keypadRoutes(KeypadButton(b), KeypadButton('A'), levels = 1).minBy { it.length }
//                route += 'A'
            }
            minRoute = min(minRoute, routeLen)
            minRouteStr = if (minRouteStr == null || minRouteStr.length > route.length) route else minRouteStr
        }
        println("minRoute=$minRoute")
        println("minRouteStr=$minRouteStr")

        var codeInputs = listOf(code)
        for (function in chain) {
            val newCodeInputs = mutableListOf<String>()
            for (codeInput in codeInputs) {
                newCodeInputs += function(codeInput)
            }
            codeInputs = newCodeInputs
//            println("codeInputs=$codeInputs")
        }

        val shortestDirectionalKeypad2Directions = codeInputs.minBy { it.length }

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