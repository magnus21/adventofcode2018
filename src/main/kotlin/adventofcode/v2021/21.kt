package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.math.max
import kotlin.system.measureTimeMillis

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {

        val time1 = measureTimeMillis {
            val players = parseInput(FileParser.getFileRows(2021, "21.txt")).toMutableList()
            var lastRoll = 0
            var turn = 0
            while (players[0].second < 1000 && players[1].second < 1000) {
                val rollSum = 3 * lastRoll + 6
                lastRoll += 3

                val playerIndex = turn++ % 2
                players[playerIndex] = updatePlayerState(players[playerIndex], rollSum)
            }
            val loserScore = players.minOf { it.second }

            println("answer part 1: ${3 * turn * loserScore}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val players = parseInput(FileParser.getFileRows(2021, "21.txt"))
            val (playerOneWins, playerTwoWins) = rollDice(players, 0, 1)

            println("playerOneWins: $playerOneWins, playerTwoWins: $playerTwoWins")
            println("answer part 2: ${max(playerOneWins, playerTwoWins)}")
        }
        println("Time: $time2 ms")
    }

    // 3:1, 4:3, 5:6, 6:7, 7:6, 8:3, 9:1 (1,3,6,7,6,3,1) <=> (3,4,5,6,7,8,9)
    private val diracDiceThreeTimesRollSums =
        listOf(3, 4, 5, 4, 5, 6, 5, 6, 7, 4, 5, 6, 5, 6, 7, 6, 7, 8, 5, 6, 7, 6, 7, 8, 7, 8, 9)
            .groupBy { it }
            .map { Pair(it.key, it.value.size) }
            .toMap()

    private fun rollDice(
        players: List<Pair<Int, Int>>,
        rollSum: Int,
        turn: Int
    ): Pair<Long, Long> {
        val playerIndex = turn % 2
        val updatedPlayer = if (turn > 1) updatePlayerState(players[playerIndex], rollSum) else players[playerIndex]

        if (updatedPlayer.second >= 21) {
            return if (playerIndex == 0) Pair(1, 0) else Pair(0, 1)
        }

        return diracDiceThreeTimesRollSums.keys.map { roll ->
            val playersList =
                if (playerIndex == 0) listOf(updatedPlayer, players[1]) else listOf(players[0], updatedPlayer)
            val res = rollDice(playersList, roll, turn + 1)
            Pair(diracDiceThreeTimesRollSums[roll]!! * res.first, diracDiceThreeTimesRollSums[roll]!! * res.second)

        }.reduceRight { a, b -> Pair(a.first + b.first, a.second + b.second) }
    }

    private fun updatePlayerState(player: Pair<Int, Int>, rollSum: Int): Pair<Int, Int> {
        var newPos = player.first + rollSum
        newPos = if (newPos % 10 == 0) 10 else newPos % 10
        return Pair(newPos, player.second + newPos)
    }

    private fun parseInput(rows: List<String>): List<Pair<Int, Int>> {
        return listOf(
            Pair(rows[0].split(" ")[4].toInt(), 0),
            Pair(rows[1].split(" ")[4].toInt(), 0)
        )
    }
}