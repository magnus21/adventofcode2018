package adventofcode.v2023

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day2 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val games = parseGames(FileParser.getFileRows(2023, "2.txt"))

        printResult("part 1") { part1(games) }
        printResult("part 2") { part2(games) }
    }

    private fun part1(games: List<Map<String, List<Int>>>): Int {
        val bag = mapOf(Pair("red", 12), Pair("green", 13), Pair("blue", 14))
        return games.mapIndexed { i, game ->
            if (bag.all {
                    val color = it.key
                    it.value >= game.getOrDefault(color, emptyList()).maxOrNull()!!
                }) i + 1 else 0
        }.sum()
    }

    private fun part2(games: List<Map<String, List<Int>>>) =
        games
            .mapIndexed { i, game ->
                game.map { it.value.maxOrNull()!! }.reduce(Int::times)
            }.sum()

    private fun parseGames(fileRows: List<String>): List<Map<String, List<Int>>> {
        return fileRows.map { row ->
            val parts = row.split(":")[1].split(";", ",").map { it.trim() }
            parts
                .map { part ->
                    val subParts = part.split(" ").map { it.trim() }
                    Pair(subParts[0].toInt(), subParts[1])
                }
                .groupBy({ it.second }, { it.first })
        }
    }

}