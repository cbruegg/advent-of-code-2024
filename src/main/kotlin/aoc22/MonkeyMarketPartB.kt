package aoc22

import kotlin.math.max

fun main() {
    val input = """
        1
        2
        3
        2024
    """.trimIndent().lines().map { it.toLong() }
//    val input = File("inputs/aoc22/input.txt").readLines().map { it.toLong() }

    val numberOfCombinations = generate4Tuples().count()
    println("numberOfCombinations=$numberOfCombinations")

    // TODO Parallelization
    // Nice test value: sequenceOf(intArrayOf(-2, 1, -1, 3))
    var maxRevenue = -1
    var lastProgress = -1
    for ((idx, wantedPriceChanges) in generate4Tuples().withIndex()) {
        val progressPct = (idx.toDouble() * 100 / numberOfCombinations).toInt()
        if (lastProgress < progressPct) {
            println("$progressPct %")
            lastProgress = progressPct
        }

        var revenue = 0
        for (buyerSeed in input) {
            val digitsAndDiffs = prng(seed = buyerSeed, includeSeed = true)
                .take(2000)
                .toDigitDiffSequence()
            var lastFourDiffs = intArrayOf()
            var priceAtSale = -1
            for ((digit, diff) in digitsAndDiffs) {
                lastFourDiffs = lastFourDiffs.push(diff, maxSize = 4)
                if (wantedPriceChanges.contentEquals(lastFourDiffs)) {
                    priceAtSale = digit
                    break
                }
            }
            if (priceAtSale != -1) {
//                println("Buyer with seed=$buyerSeed buys at priceAtSale=$priceAtSale")
                revenue += priceAtSale
            } else {
//                println("Buyer with seed=$buyerSeed does not buy")
            }
        }
        maxRevenue = max(maxRevenue, revenue)
    }
    println("maxRevenue=$maxRevenue")
}

fun IntArray.push(element: Int, maxSize: Int): IntArray =
    if (size + 1 < maxSize) {
        copyOf(size + 1).also { it[it.lastIndex] = element }
    } else {
        val next = IntArray(maxSize)
        copyInto(next, startIndex = 1)
        next[maxSize - 1] = element
        next
    }

fun generate4Tuples(): Sequence<IntArray> = sequence {
    for (d1 in -9..9) {
        for (d2 in -9..9) {
            for (d3 in -9..9) {
                for (d4 in -9..9) {
                    yield(intArrayOf(d1, d2, d3, d4))
                }
            }
        }
    }
}

data class DigitAndDiff(val digit: Int, val diff: Int)

fun Sequence<Long>.toDigitDiffSequence(): Sequence<DigitAndDiff> = sequence {
    var gotFirst = false
    var prev = 0
    for (secret in this@toDigitDiffSequence) {
        val digit = (secret % 10).toInt()
        if (gotFirst) {
            yield(DigitAndDiff(digit, digit - prev))
            prev = digit
        } else {
            prev = digit
            gotFirst = true
        }
    }
}

fun debug(seed: Long, iterations: Int = 10) {
    val secrets = mutableListOf(seed)
    val digits = mutableListOf((seed % 10).toInt())
    val digitDiffs = mutableListOf<Int?>(null)
    for (next in prng(seed).take(iterations - 1)) {
        val digit = (next % 10).toInt()
        digitDiffs += digit - digits.last()
        digits += digit
        secrets += next
    }
    val maxSecretLength = secrets.maxOf { it.toString().length }
    for (i in secrets.indices) {
        val digitDiff = digitDiffs[i]?.let { " ($it)" } ?: ""
        println("${secrets[i].toString().padStart(maxSecretLength)}: ${digits[i]}$digitDiff")
    }
}