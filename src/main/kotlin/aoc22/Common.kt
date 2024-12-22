package aoc22

fun prng(seed: Long, includeSeed: Boolean = false): Sequence<Long> = sequence {
    if (includeSeed) yield(seed)
    var cur = seed
    while (true) {
        cur = ((cur * 64) xor cur).rem(16777216)
        cur = ((cur / 32) xor cur).rem(16777216)
        cur = ((cur * 2048) xor cur).rem(16777216)
        yield(cur)
    }
}

fun withPrng(seed: Long, includeSeed: Boolean = false, length: Int, block: (Long) -> Boolean) {
    if (includeSeed && !block(seed)) return
    var elementsLeft = if (includeSeed) length - 1 else length
    var cur = seed
    while (--elementsLeft >= 0) {
        cur = ((cur * 64) xor cur).rem(16777216)
        cur = ((cur / 32) xor cur).rem(16777216)
        cur = ((cur * 2048) xor cur).rem(16777216)
        if (!block(cur)) return // allow early termination
    }
}

