package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {
        val (template, insertionRules) = parseInput(FileParser.getFileRows(2021, "14.txt"))

        val time1 = measureTimeMillis {
            val elementCount = getInitialElementCount(template)

            var polymer = template.toCharArray().toList()
            for (step in 1..10) {
                polymer = expandPolymer(elementCount, polymer, insertionRules)
            }

            val sortedCounts = elementCount.values.sorted()

            println("answer part 1: ${sortedCounts.last() - sortedCounts.first()}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val elementCount = getInitialElementCount(template)

            val pairCounts = mutableMapOf<String, Long>()
            boostrapPairsMap(template.toCharArray().toList(), pairCounts)

            for (step in 1..40) {
                pairCounts.toMap().forEach { pair ->
                    val pairKey = pair.key
                    val charToInsert = insertionRules[pairKey]!!

                    elementCount[charToInsert] = elementCount.getOrDefault(charToInsert, 0) + pair.value

                    val newLeftPair = pairKey[0].toString() + charToInsert
                    pairCounts[newLeftPair] = pairCounts.getOrDefault(newLeftPair, 0) + pair.value

                    val newRightPair = charToInsert.toString() + pairKey[1]
                    pairCounts[newRightPair] = pairCounts.getOrDefault(newRightPair, 0) + pair.value

                    pairCounts[pairKey] = pairCounts.getOrDefault(pairKey, 0) - pair.value
                }
            }

            val sortedCounts = elementCount.values.sorted()

            println("answer part 2: ${sortedCounts.last() - sortedCounts.first()}")
        }
        println("Time: $time2 ms")
    }

    private fun getInitialElementCount(template: String) = template.toCharArray().toList()
        .groupingBy { it }
        .eachCount()
        .map { Pair(it.key, it.value.toLong()) }
        .toMap()
        .toMutableMap()

    private fun boostrapPairsMap(
        template: List<Char>,
        pairs: MutableMap<String, Long>
    ) {
        for (i in template.drop(1).indices) {
            val key = template[i].toString() + template[i + 1]
            pairs[key] = pairs.getOrDefault(key, 0) + 1
        }
    }

    private fun expandPolymer(
        elementCount: MutableMap<Char, Long>,
        polymer: List<Char>,
        insertionRules: Map<String, Char>
    ): List<Char> {
        val newPolymer = mutableListOf<Char>()
        for (i in polymer.drop(1).indices) {
            val current = polymer[i]
            val next = polymer[i + 1]

            val charToInsert = insertionRules[current.toString() + next]!!

            elementCount[charToInsert] = elementCount.getOrDefault(charToInsert, 0) + 1
            newPolymer.add(current)
            newPolymer.add(charToInsert)

        }
        return newPolymer.plus(polymer.last())
    }

    private fun parseInput(rows: List<String>): Pair<String, Map<String, Char>> {
        val insertionRules = rows.drop(2).associate { row ->
            val parts = row.split("->").map { it.trim() }
            Pair(parts[0], parts[1].toCharArray()[0])
        }
        return Pair(rows.first(), insertionRules)
    }
}