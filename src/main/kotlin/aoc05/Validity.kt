package aoc05

fun findViolatedRule(update: List<Int>, rules: Map<Int, Set<Int>>): RuleViolation? {
    val allPagesInUpdate = update.toSet() // for fast lookup
    val seenPages = mutableSetOf<Int>()
    for ((index, page) in update.withIndex()) {
        val prerequiredPages = rules[page] ?: emptySet()
        for (prerequiredPage in prerequiredPages) {
            if (prerequiredPage in allPagesInUpdate && prerequiredPage !in seenPages) {
                return RuleViolation(
                    prerequiredPage,
                    page,
                    pageIndex = index,
                    prerequiredPageIndex = update.indexOf(prerequiredPage)
                )
            }
        }
        seenPages += page
    }
    return null
}

data class RuleViolation(
    val prerequiredPage: Int,
    val page: Int,
    val pageIndex: Int,
    val prerequiredPageIndex: Int
)