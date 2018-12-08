package adventofcode

import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val polymer = File("src/main/resources/5.txt").readText()

    val time1 = measureTimeMillis {
        val result = polymer.fold("") { acc, letter -> processLetter(acc, letter) }

        println("Polymer size: " + polymer.length)
        println("Result size: " + result.length)
    }

    println(time1)

    val time2 = measureTimeMillis {
        // What is the length of the shortest polymer you can produce by removing all units of exactly one type and fully reacting the result?
        val resultValue =
            polymer.map(Char::toLowerCase).distinct().map {
                Pair(
                    it,
                    polymer
                        .filter { letter -> letter.toLowerCase() != it }
                        .fold("") { acc, letter -> processLetter(acc, letter) }.length
                )
            }.sortedBy { it.second }[0]

        println("Result after removing unit (unit, polymer size): $resultValue")
    }

    println(time2)
}

fun processLetter(acc: String, letter: Char): String {
    //println("acc: " +  acc + ", letter: " + letter)
    if (acc.isEmpty()) {
        return letter.toString();
    }
    if (hasReaction(acc.last(), letter)) {
        return acc.dropLast(1)
    }
    return acc + letter
}

private fun hasReaction(last: Char, letter: Char) =
    last.equals(letter, true) && !last.equals(letter, false)
