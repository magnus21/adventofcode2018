package adventofcode.v2015

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2015, "13.txt")

        val time1 = measureTimeMillis {
            val happinessRelations = parseInput(input)

            val persons = happinessRelations.map { it.key.first }.toSet()
            val permutations = AdventOfCodeUtil.generatePermutations(persons.toList())

            val answer = permutations.map { permutation ->
                permutation.sumOf { person ->
                    getNeighbours(person, permutation)
                        .sumBy { neighbour -> happinessRelations[Pair(person, neighbour)] ?: error("Oops!") }
                }
            }.maxOrNull()

            println("Part 1: $answer ")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val happinessRelations = parseInput(input).toMutableMap()
            val persons = happinessRelations.map { it.key.first }.toMutableSet()

            happinessRelations.putAll(persons.map { Pair(Pair("Me", it), 0) })
            happinessRelations.putAll(persons.map { Pair(Pair(it,"Me"), 0) })
            persons.add("Me")

            val permutations = AdventOfCodeUtil.generatePermutations(persons.toList())

            val answer = permutations.map { permutation ->
                permutation.map { person ->
                    getNeighbours(person, permutation)
                        .sumBy { neighbour -> happinessRelations[Pair(person, neighbour)] ?: error("Oops!") }
                }.sum()
            }.maxOrNull()

            println("Part 2: $answer ")
        }
        println("Time: $time2 ms")
    }

    private fun getNeighbours(person: String, seating: List<String>): List<String> {
        return when (val index = seating.indexOf(person)) {
            0 -> listOf(seating[1], seating[seating.size - 1])
            seating.size - 1 -> listOf(seating[0], seating[index - 1])
            else -> listOf(seating[index - 1], seating[index + 1])
        }
    }

    private fun parseInput(input: List<String>): Map<Pair<String, String>, Int> {
        return input.map {
            val parts = it.split(' ')
            val sign = if (parts[2] == "gain") 1 else -1

            Pair(Pair(parts[0], parts[10].dropLast(1)), sign * parts[3].toInt())
        }.toMap()
    }
}