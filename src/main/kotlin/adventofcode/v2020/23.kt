package adventofcode.v2020

import adventofcode.util.LinkedList
import kotlin.system.measureTimeMillis

object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = "974618352".toList().map { it.toString().toInt() }

        val time = measureTimeMillis {
            println("Part 1: ${part1(input)}")
            println("Part 2: ${part2(input)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(input: List<Int>): String {
        val cups = LinkedList.fromList(input)
        cups.connectCurrentToHead()
        cups.startFromHead()
        cups.generateLookupMap()

        val lowestCupValue = input.min()!!
        val highestCupValue = input.max()!!

        doMoves(cups, lowestCupValue, highestCupValue, 100, true)

        println("\n-- final --")
        printCups(cups, cups.getCurrent().value)
        // too high: 81247840320
        cups.goto(1)
        return cups.toListStartAtCurrent().drop(1).joinToString("")
    }

    private fun part2(input: List<Int>): Long {

        val lowestCupValue = input.min()!!
        val highestCupValue = 1000000
        val nrOfMoves = 10000000

        val cupsAsList = input.plus((input.max()!! + 1)..highestCupValue)
        val cups = LinkedList.fromList(cupsAsList)
        cups.connectCurrentToHead()
        cups.startFromHead()
        cups.generateLookupMap()

        doMoves(cups, lowestCupValue, highestCupValue, nrOfMoves, false)

        println("\n-- final --")
        printCups(cups, cups.getCurrent().value)

        cups.goto(1)

        return cups.removeNext().value.toLong() * cups.removeNext().value.toLong()
    }

    private fun doMoves(
        cups: LinkedList<Int>,
        lowestCupValue: Int,
        highestCupValue: Int,
        nrOfMoves: Int,
        printDebug: Boolean = false
    ) {
        (1..nrOfMoves).forEach { moveNr ->

            if (moveNr % 1000000 == 0) {
                println("\n-- move $moveNr --")
            }
            if (printDebug) println("\n-- move $moveNr --")

            val currentCupValue = cups.getCurrent().value

            if (printDebug) printCups(cups, currentCupValue)

            val pickedUpCups = (1..3).map {
                cups.removeNext()
            }
            if (printDebug) println("pick up: ${pickedUpCups.joinToString(", ")}")

            // Goto destination cup
            var dest = currentCupValue - 1
            val pickedUpCupsValues = pickedUpCups.map { it.value }
            while (pickedUpCupsValues.contains(dest) || cups.goto(dest) == null) {
                if (dest - 1 < lowestCupValue) {
                    dest = highestCupValue
                } else {
                    dest--
                }
            }

            if (printDebug) println("destination: ${cups.getCurrent().value}")

            pickedUpCups.forEach { cups.insertNode(it) }

            cups.goto(currentCupValue)
            cups.stepToNext()
        }
    }

    private fun printCups(cups: LinkedList<Int>, currentCupValue: Int) {
        val cupsAsString =
            cups.toList(100).joinToString(" ") { if (it == currentCupValue) "($it)" else it.toString() }
        println("cups: $cupsAsString")
    }
}