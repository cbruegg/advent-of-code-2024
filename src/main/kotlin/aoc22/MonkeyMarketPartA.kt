package aoc22

fun main() {
    val input = """
        1
        10
        100
        2024
    """.trimIndent().lines()

    val sumOf2000thSecret = input.map { it.toLong() }.sumOf { seed -> prng(seed).drop(1999).first() }
    println(sumOf2000thSecret)

    val next = prng(seed = 123).take(10).toList()
    println(next)
}

fun prng(seed: Long): Sequence<Long> = sequence {
    var cur = seed
    while (true) {
        cur = ((cur * 64) xor cur).rem(16777216)
        cur = ((cur / 32) xor cur).rem(16777216)
        cur = ((cur * 2048) xor cur).rem(16777216)
        yield(cur)
    }
}

