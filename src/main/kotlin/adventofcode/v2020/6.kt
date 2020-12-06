package adventofcode.v2020

import adventofcode.util.FileParser

object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getAsString(2020, "6.txt")

        part1(input)
        part2(input)
    }

    private fun part1(input: String) {
        val sum = input.split("\n\n")
            .map { it.replace("\n", "").toSet().count() }
            .sum()
        println("Part 1: $sum")
    }

    private fun part2(input: String) {
        val sum = input.split("\n\n")
            .map { Pair(it.split("\n").size, it.replace("\n", "").groupByTo(mutableMapOf()) { it }) }
            .map { it.second.filter { entry -> entry.value.size == it.first }.size }
            .sum()

        println("Part 2: $sum") // 3052
    }
}


