package aoc22

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.pow

suspend fun main() {
    val input = File("inputs/aoc22/input.txt").readLines().map { it.toLong() }

    val numberOfCombinations = (-9..9).count().toDouble().pow(4).toInt()
    println("numberOfCombinations=$numberOfCombinations")

    // Nice test value: sequenceOf(intArrayOf(-2, 1, -1, 3))
    val maxRevenue = AtomicInteger(-1)
    val lastProgress = AtomicInteger(-1)
    forEach4Tuple { idx, d0, d1, d2, d3 ->
        var targetSequence = LastFourDiffs()
        targetSequence = targetSequence.push(d0)
        targetSequence = targetSequence.push(d1)
        targetSequence = targetSequence.push(d2)
        targetSequence = targetSequence.push(d3)

        val progressPct = (idx.toDouble() * 100 / numberOfCombinations).toInt()
        if (lastProgress.getAndSet(progressPct) < progressPct) {
            println("$progressPct %")
        }

        var revenue = 0
        for (buyerSeed in input) {
            var lastFourDiffs = LastFourDiffs()
            var priceAtSale = -1
            var seenDiffs = 0
            forEachDigitDiffSequence(buyerSeed, includeSeed = true, length = 2001 /* 2000 plus seed */) { digit, diff ->
                lastFourDiffs = lastFourDiffs.push(diff)
                if (++seenDiffs >= 4 && lastFourDiffs == targetSequence) {
                    priceAtSale = digit
                    false
                } else {
                    true
                }
            }
            if (priceAtSale != -1) {
                revenue += priceAtSale
            }
        }
        maxRevenue.updateAndGet { prev -> max(prev, revenue) }
    }
    println("maxRevenue=$maxRevenue")
}

@JvmInline
value class LastFourDiffs(private val storage: Int = 0) {
    // We need 5 bits to store range 0..18.
    // 4 values of 5 bits fit into 20 bits, so Int32 as storage is fine.

    fun push(diff: Int): LastFourDiffs {
        val unsignedDiff = diff + 9 // map range -9..9 to 0..18 to avoid sign bit

        return LastFourDiffs(
            ((storage shl 5) or unsignedDiff) and 0b00000000000011111111111111111111
        )
    }

    fun toArray() = intArrayOf(
        ((storage shr 15) and 0b11111) - 9,
        ((storage shr 10) and 0b11111) - 9,
        ((storage shr 5) and 0b11111) - 9,
        ((storage shr 0) and 0b11111) - 9,
    )
}

suspend fun forEach4Tuple(block: (index: Int, d0: Int, d1: Int, d2: Int, d3: Int) -> Unit) = coroutineScope {
    val i = AtomicInteger(0)
    for (d0 in -9..9) {
        (-9..9).map { d1 ->
            launch {
                for (d2 in -9..9) {
                    for (d3 in -9..9) {
                        block(i.incrementAndGet(), d0, d1, d2, d3)
                    }
                }
            }
        }.joinAll()
    }
}

fun forEachDigitDiffSequence(
    seed: Long,
    includeSeed: Boolean,
    length: Int,
    block: (digit: Int, diff: Int) -> Boolean
) {
    var gotFirst = false
    var prev = 0
    withPrng(seed, includeSeed, length) { secret ->
        val digit = (secret % 10).toInt()
        if (gotFirst) {
            if (!block(digit, digit - prev)) {
                // Allow breaking out of the loop
                return@withPrng false
            }
            prev = digit
        } else {
            prev = digit
            gotFirst = true
        }
        true
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