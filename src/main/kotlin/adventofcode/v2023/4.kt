package adventofcode.v2023

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.pow
import kotlin.time.ExperimentalTime

object Day4 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val cards = parseCards(FileParser.getFileRows(2023, "4.txt"))

        printResult("part 1") { part1(cards) }
        printResult("part 2") { part2(cards) } // 24 s without cache.
    }

    private fun part1(cards: List<Card>): Int {
        return cards.sumOf { card ->
            val wins = getWinningNumbers(card)
            if (wins > 0)
                if (wins == 1) 1
                else 2.0.pow(wins - 1.0).toInt()
            else 0
        }
    }

    private val winsCache = mutableMapOf<Int, Int>()
    private fun getWinningNumbers(card: Card): Int {
        if (!winsCache.containsKey(card.number)) {
            winsCache[card.number] = card.myNumbers.intersect(card.winningNumbers.toSet()).size
        }
        return winsCache[card.number]!!
    }

    private fun part2(cards: List<Card>): Int {
        return cards.sumOf { card ->
            1 + getWinsForCard(card, cards)
        }
    }

    private fun getWinsForCard(card: Card, cards: List<Card>): Int {
        val wins = getWinningNumbers(card)
        if (wins == 0) {
            return 0
        }
        return wins + cards.subList(card.number, card.number + wins)
            .sumOf { getWinsForCard(it, cards) }
    }

    private fun parseCards(fileRows: List<String>): List<Card> {
        return fileRows.map { row ->
            val parts1 = row.split("|", ":")
            val wins = parts1[1].split(" ").filter { !it.isBlank() }.map { it.toInt() }
            val my = parts1[2].split(" ").filter { !it.isBlank() }.map { it.toInt() }

            Card(parts1[0].split(" ").last().toInt(), wins, my)
        }
    }

    data class Card(val number: Int, val winningNumbers: List<Int>, val myNumbers: List<Int>)

}