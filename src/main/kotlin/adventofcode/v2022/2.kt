package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.v2022.Day2.RPC.*
import kotlin.time.ExperimentalTime

object Day2 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "2.txt").map {
            val parts = it.split(" ")
            parts[0] to parts[1]
        }

        printResult("part 1") {
            input.map { toRPC(it.first) to toRPC(it.second) }.sumOf { getScore(it) }
        }

        printResult("part 1") {
            input.map { toRPC(it.first) to toRPC2(it) }.sumOf { getScore(it) }
        }
    }

    private fun getScore(pair: Pair<RPC, RPC>): Int =
        when {
            pair.first == ROCK && pair.second == PAPER -> 6
            pair.first == PAPER && pair.second == SCISSORS -> 6
            pair.first == SCISSORS && pair.second == ROCK -> 6
            pair.first == pair.second -> 3
            else -> 0
        } + pair.second.score

    private fun toRPC(name: String): RPC {
        return when (name) {
            "A" -> ROCK
            "B" -> PAPER
            "X" -> ROCK
            "Y" -> PAPER
            else -> SCISSORS
        }
    }

    private fun toRPC2(pair: Pair<String, String>): RPC {
        return when (pair.first) {
            "A" -> when (pair.second) {
                "X" -> SCISSORS
                "Y" -> ROCK
                else -> PAPER
            }
            "B" -> when (pair.second) {
                "X" -> ROCK
                "Y" -> PAPER
                else -> SCISSORS
            }
            else -> when (pair.second) {
                "X" -> PAPER
                "Y" -> SCISSORS
                else -> ROCK
            }
        }
    }

    enum class RPC(val score: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3)
    }
}