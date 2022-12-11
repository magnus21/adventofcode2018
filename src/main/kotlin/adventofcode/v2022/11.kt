package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day11 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "11.txt")

        printResult("part 1") { part1(parseInput(input)) }
        printResult("part 2") { part2(parseInput(input)) }
    }

    private fun part1(monkeys: List<Monkey>): Long {
        return runRounds(monkeys, 20) { operation, item -> getWorryLevel(operation, item) / 3 }
    }

    private fun part2(monkeys: List<Monkey>): Long {
        val dividerGcd = monkeys.map { it.divider }.reduce { acc, no -> acc * no }
        return runRounds(monkeys, 10000) { operation, item ->
            getWorryLevel(operation, if (item > dividerGcd) item % dividerGcd else item)
        }
    }

    private fun runRounds(monkeys: List<Monkey>, nrRounds: Int, worryLevelFunction: (String, Long) -> Long): Long {
        val inspectCount = mutableMapOf<String, Long>()
        (1..nrRounds).forEach { _ ->
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    val newItem = worryLevelFunction.invoke(monkey.operation, item)
                    val toMonkey = monkey.toMonkey(newItem)
                    monkeys[toMonkey].items.add(newItem)
                }
                inspectCount[monkey.name] = inspectCount.getOrDefault(monkey.name, 0) + monkey.items.size
                monkey.items.clear()
            }
        }
        val sortedCounts = inspectCount.values.sortedDescending()
        return sortedCounts[0] * sortedCounts[1]
    }

    private fun getWorryLevel(operation: String, item: Long): Long {
        val parts = operation.split(" ")
        val operator1 = getOperator(parts[0], item)
        val operator2 = getOperator(parts[2], item)

        return when (parts[1]) {
            "+" -> operator1 + operator2
            else -> operator1 * operator2
        }
    }

    private fun getOperator(str: String, item: Long) = if (str == "old") item else str.toLong()

    private fun parseInput(input: List<String>): List<Monkey> {
        return input.chunked(7).map { monkey ->
            val name = monkey[0].dropLast(1)
            val items = monkey[1]
                .substringAfter(":")
                .replace(" ", "")
                .split(",")
                .map { it.toLong() }
                .toMutableList()

            val operation = monkey[2].substringAfter("new = ")
            val divider = monkey[3].split(" ").last().toLong()
            val trueMonkey = monkey[4].split(" ").last().toInt()
            val falseMonkey = monkey[5].split(" ").last().toInt()
            val toMonkeyFunction = { input: Long ->
                if (input % divider == 0L) trueMonkey
                else falseMonkey
            }

            Monkey(name, items, operation, toMonkeyFunction, divider)
        }
    }

    data class Monkey(
        val name: String,
        val items: MutableList<Long>,
        val operation: String,
        val toMonkey: (Long) -> Int,
        val divider: Long
    )
}