package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day5 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val (rules, pageUpdates) = parseInput(FileParser.getFileRows(2024, "5.txt"))

        val distinctPages = rules.flatMap { listOf(it.first, it.second) }.toSet()
        val rulesPerPage = distinctPages.associateWith { rules.filter { r -> r.first == it } }

        printResult("part 1") { part1(rulesPerPage, pageUpdates) }
        printResult("part 2") { part2(rulesPerPage, pageUpdates) }
    }

    private fun part1(rulesPerPage: Map<Int, List<Pair<Int, Int>>>, pageUpdates: List<List<Int>>): Int {
        return getPageUpdates(rulesPerPage, pageUpdates, true).sumOf { it[it.size / 2] }
    }

    private fun part2(rulesPerPage: Map<Int, List<Pair<Int, Int>>>, pageUpdates: List<List<Int>>): Int {
        val incorrectPageUpdates = getPageUpdates(rulesPerPage, pageUpdates, false)

        return incorrectPageUpdates.map { row ->
            val pageIndicies = row.mapIndexed { i, n -> Pair(n, i) }.toMap()
            val applicableRules = getApplicableRules(row, rulesPerPage, pageIndicies)

            // Use rule count per number to sort (last number not needed).
            applicableRules.asSequence().map { it.first }.groupBy { it }.toList()
                .sortedBy { it.second.size }
                .map { it.first }
                .toList()
        }.sumOf { it[it.size / 2 - 1] }
    }

    private fun getPageUpdates(
        rulesPerPage: Map<Int, List<Pair<Int, Int>>>,
        pageUpdates: List<List<Int>>,
        correctUpdates: Boolean
    ): List<List<Int>> {
        val correctPageUpdates = pageUpdates.filter { row ->
            val pageIndicies = row.mapIndexed { i, n -> Pair(n, i) }.toMap()
            val applicableRules = getApplicableRules(row, rulesPerPage, pageIndicies)

            val result = applicableRules.all { rule -> pageIndicies[rule.first]!! < pageIndicies[rule.second]!! }
            if (correctUpdates) result else !result
        }
        return correctPageUpdates
    }

    private fun getApplicableRules(
        row: List<Int>,
        rulesPerPage: Map<Int, List<Pair<Int, Int>>>,
        pageIndicies: Map<Int, Int>
    ) = row.flatMap { page ->
        rulesPerPage[page]!!.filter { pageIndicies.containsKey(it.first) && pageIndicies.containsKey(it.second) }
    }.toSet()

    private fun parseInput(fileRows: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val rules = fileRows.takeWhile { it.isNotBlank() }
            .map {
                val parts = it.split("|")
                Pair(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]))
            }

        val pageUpdates = fileRows.drop(rules.size + 1)
            .map { pageUpdate -> pageUpdate.split(",").map { Integer.parseInt(it) } }

        return Pair(rules, pageUpdates)
    }
}