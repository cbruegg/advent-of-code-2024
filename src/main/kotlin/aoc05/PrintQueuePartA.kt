package aoc05

fun main() {
    val input = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent()
    val lines = input.lines()

    // For each page, this contains the set of prerequired pages
    val rules: Map<Int, Set<Int>> = lines
        .asSequence()
        .filter { "|" in it }
        .map { it.split("|") }
        .map { (a, b) -> Pair(a.toInt(), b.toInt()) }
        .groupBy(
            keySelector = { (_, page) -> page },
            valueTransform = { (prerequiredPage, _) -> prerequiredPage }
        )
        .mapValues { (_, prerequiredPages) -> prerequiredPages.toSet() }

    val updates: List<List<Int>> = lines
        .asSequence()
        .filter { "|" !in it && it.isNotBlank() }
        .map { line -> line.split(",").map { it.toInt() } }
        .toList()

    val validUpdates = updates.filter { isValidUpdate(it, rules) }
    val sumOfMiddlePages = validUpdates.sumOf { it.middleElement }

    println(sumOfMiddlePages)
}

fun isValidUpdate(update: List<Int>, rules: Map<Int, Set<Int>>): Boolean {
    val allPagesInUpdate = update.toSet() // for fast lookup
    val seenPages = mutableSetOf<Int>()
    for (page in update) {
        val prerequiredPages = rules[page] ?: emptySet()
        for (prerequiredPage in prerequiredPages) {
            if (prerequiredPage in allPagesInUpdate && prerequiredPage !in seenPages) {
                return false
            }
        }
        seenPages += page
    }
    return true
}

val <T> List<T>.middleElement: T
    get() {
        check(size % 2 == 1) { "Cannot find middle element for list of even size" }
        val middleIndex = size / 2
        return this[middleIndex]
    }