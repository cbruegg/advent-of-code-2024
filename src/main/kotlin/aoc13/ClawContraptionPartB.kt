package aoc13

import java.io.File

fun main() {
    val input = File("inputs/aoc13/input.txt").readText()

    val games = parseClawGames(input).map { fixGame(it) }
    val solutionsByGame = games.map { it.solve() }
    val cheapestSolutions = solutionsByGame.map { solutions -> solutions.minByOrNull { it.cost } }
    val totalCost = cheapestSolutions.sumOf { it?.cost ?: 0 }
    println(totalCost)
}

fun fixGame(game: ClawGame): ClawGame =
    game.copy(prizeX = game.prizeX + 10000000000000, prizeY = game.prizeY + 10000000000000)
