package adventofcode.v2019

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day22 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "22.txt")

        val shufflingCommands = parseInput(input)

        //shufflingCommands.map { it.name }.distinct().forEach(::println)

        // Run program.
        val time1 = measureTimeMillis {
            val deckSize = 10007
            val deck = (0 until deckSize).map { it }.toMutableList()

            shufflingCommands.forEach { shuffleDeck(it, deck) }

            val result = deck
                .mapIndexed { index, element -> Pair(index, element) }
                .filter { it.second == 2019 }
                .map { it.first }[0]

            println("Answer part 1: ${result}")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {

            println("Answer part 2: ${""}")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun shuffleDeck(shuffleCommand: ShufflingCommand, deck: MutableList<Int>) {
        val newDeck =
            when (shuffleCommand.name) {
                "cut" -> cut(deck, shuffleCommand.parameter!!)
                "deal with increment" -> dealWithIncrement(deck, shuffleCommand.parameter!!)
                else -> dealIntoNew(deck)
            }

        deck.clear()
        deck.addAll(newDeck)
    }

    private fun dealIntoNew(deck: MutableList<Int>): List<Int> {
        return deck.reversed()
    }

    private fun cut(deck: List<Int>, parameter: Int): List<Int> {
        return if (parameter > 0) {
            deck.takeLast(deck.size - parameter).plus(deck.take(parameter))
        } else {
            deck.takeLast(-parameter).plus(deck.take(deck.size + parameter))
        }
    }

    private fun dealWithIncrement(deck: List<Int>, parameter: Int): List<Int> {
        val newDeck = (0 until deck.size).map { it }.toMutableList()
        var pos = 0
        var count = 0
        while (count < deck.size) {
            newDeck[pos % deck.size] = deck[count]
            pos += parameter
            count++
        }

        return newDeck
    }

    private fun parseInput(input: List<String>): List<ShufflingCommand> {
        return input.map {
            val parts = it.split(" ")
            val parameter = parts[parts.size - 1].toIntOrNull()
            ShufflingCommand(parts.subList(0, parts.size - 1).joinToString(" "), parameter)
        }
    }

    private data class ShufflingCommand(var name: String, var parameter: Int?) {
        override fun toString(): String {
            return "($name, $parameter)"
        }
    }
}