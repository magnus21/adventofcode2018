package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day22 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "22.txt")

        val time = measureTimeMillis {
            println("Part 1: ${part1(parseInput(input))}")
            println("Part 2: ${part2(parseInput(input))}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(players: List<Player>): Int {

        val playerOne = players[0]
        val playerTwo = players[1]

        val winner = playGame(playerOne, playerTwo)
        return winner.cards.reversed().mapIndexed { i, c -> (i + 1) * c }.sum()
    }


    private fun part2(players: List<Player>): Int {
        val playerOne = players[0]
        val playerTwo = players[1]

        val winner = playRecursiveGame(playerOne, playerTwo)

        println("\n\n== Post-game results ==")
        println("Player 1's deck: ${playerOne.cards.joinToString(", ")}")
        println("(Player 2's deck: ${playerTwo.cards.joinToString(", ")}")
        println("\n")

        return winner.cards.reversed().mapIndexed { i, c -> (i + 1) * c }.sum()
    }

    private fun playRecursiveGame(playerOne: Player, playerTwo: Player, gameNumber: Int = 1): Player {

        println("\n=== Game $gameNumber ===")

        val previousHands = mutableSetOf<Pair<List<Int>, List<Int>>>()
        var roundNumber = 1
        while (true) {

            println("\n-- Round ${roundNumber} (Game $gameNumber) --")
            println("Player 1's deck:${playerOne.cards.joinToString(", ")}")
            println("Player 2's deck: ${playerTwo.cards.joinToString(", ")}")

            val hands = Pair(playerOne.cards, playerTwo.cards)
            // Player 1 wins game if hands has been seen before.
            if (previousHands.contains(hands)) {
                println("Found previous hand, Player 1 wins game $gameNumber")
                return playerOne
            }
            previousHands.add(hands)

            val playerOneCard = playerOne.cards[0]
            val playerTwoCard = playerTwo.cards[0]

            playerOne.cards.removeAt(0)
            playerTwo.cards.removeAt(0)

            println("Player 1 plays: $playerOneCard")
            println("Player 2 plays: $playerTwoCard")

            val winner = if (playerOneCard <= playerOne.cards.size && playerTwoCard <= playerTwo.cards.size) {
                // Recurse into sub game
                println("Playing a sub-game to determine the winner...")
                val subGameWinner = playRecursiveGame(
                    Player(playerOne.id, playerOne.cards.take(playerOneCard).toMutableList()),
                    Player(playerTwo.id, playerTwo.cards.take(playerTwoCard).toMutableList()),
                    gameNumber + 1
                )
                println("\n...anyway, back to game $gameNumber.")

                subGameWinner
            } else {
                if (playerOneCard > playerTwoCard) playerOne else playerTwo
            }

            if (winner.id == playerOne.id) {
                println("Player 1 wins round $roundNumber of game $gameNumber!")
                playerOne.cards.addAll(listOf(playerOneCard, playerTwoCard))
            } else {
                println("Player 1 wins round $roundNumber of game $gameNumber!")
                playerTwo.cards.addAll(listOf(playerTwoCard, playerOneCard))
            }

            roundNumber++

            if (playerOne.cards.size == 0) {
                println("The winner of game $gameNumber is player ${winner.id}!")
                return playerTwo
            } else if (playerTwo.cards.size == 0) {
                println("The winner of game $gameNumber is player ${winner.id}!")
                return playerOne
            }
        }
    }

    private fun playGame(playerOne: Player, playerTwo: Player): Player {
        while (true) {
            val playerOneCard = playerOne.cards[0]
            val playerTwoCard = playerTwo.cards[0]

            playerOne.cards.removeAt(0)
            playerTwo.cards.removeAt(0)

            if (playerOneCard > playerTwoCard) {
                playerOne.cards.addAll(listOf(playerOneCard, playerTwoCard))
            } else {
                playerTwo.cards.addAll(listOf(playerTwoCard, playerOneCard))
            }

            if (playerOne.cards.size == 0) {
                return playerTwo
            } else if (playerTwo.cards.size == 0) {
                return playerOne
            }
        }
    }

    private fun parseInput(input: List<String>): List<Player> {
        return input.fold(mutableListOf()) { players, line ->
            when {
                line.startsWith("Player") -> players.add(
                    Player(
                        line.replace("Player ", "").take(1).toInt(),
                        mutableListOf()
                    )
                )
                else -> {
                    if (line != "") {
                        players.last().cards.add(line.toInt())
                    }
                }
            }
            players
        }
    }

    data class Player(val id: Int, val cards: MutableList<Int>)
}