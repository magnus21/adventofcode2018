package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2015, "15.txt")

        val ingredients = parseInput(input)

        val time1 = measureTimeMillis {

            val answer = ""

            println("Part 1: $answer ")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            val answer = ""

            println("Part 2: $answer ")
        }
        println("Time: $time2 ms")
    }

    data class Ingredient(val name: String, val properties: List<Pair<String, Int>>)


    private fun parseInput(input: List<String>): List<Ingredient> {
        return input.map { row ->
            val parts = row.split(": ")
            val subParts = parts[1].split(", ")
            Ingredient(parts[0], subParts.map { Pair(it.split(' ').first(), it.split(' ').last().toInt()) })
        }
    }
}