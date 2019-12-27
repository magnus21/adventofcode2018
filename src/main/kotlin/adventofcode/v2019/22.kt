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
            val n = 119315717514047L.toBigInteger()

            // Find the combined polynomial for all shuffles: x1 = a*x0 + b
            var a = 1L
            var b = 0L

            shufflingCommands.forEach {
                val pair = when (it.name) {
                    "cut" -> Pair(1, -it.parameter!!)
                    "deal with increment" -> Pair(it.parameter!!, 0)
                    else -> Pair(-1, -1)
                }

                // a1 * (a0 * x + b0) + b1 == a1 * a0 * x + a1 * b0 + b1
                // The `% n` doesn't change the result, but keeps the numbers small.
                a = (pair.first * a) % n.toLong()
                b = (pair.first * b + pair.second) % n.toLong()
            }

            val position = 2020
            val m = 101741582076661

            val ma = a.toBigInteger().modPow(m.toBigInteger(), n)
            val mb = (b.toBigInteger() * (ma.minus(1.toBigInteger()) * (a - 1).toBigInteger().modInverse(n))) % n

            // This computes "where does 2020 end up", but I want "what is at 2020".
            //print((ma.times(position.toBigInteger() + mb) % n))

            // So need to invert (2020 - MB) * inv(Ma)
            println("Answer part 2: ${(position.toBigInteger().minus(mb) * ma.modInverse(n)) % n}")
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