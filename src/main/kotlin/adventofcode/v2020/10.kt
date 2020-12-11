package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "10.txt").map { it.toInt() }

        // Run program.
        val time = measureTimeMillis {
            println("Part 1: ${part1(input)}")
            println("Part 2: ${part2(input)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(input: List<Int>): Int {

        val adapters = mutableListOf(3)
        input.sorted().fold(0) { prev, adapter ->
            adapters.add(adapter - prev)
            adapter
        }

        val map = adapters.groupBy { it }.map { Pair(it.key, it.value.size) }.toMap()

        return map.getOrElse(1) { 0 } * map.getOrElse(3) { 0 }
    }

    // Too low: 110 534 070 370 304
    private fun part2(input: List<Int>): Long {
        // Don't forget the starting point 0!!!
        val sortedAdapters = input.plus(0).sorted()
        val childrenCountMap = mutableMapOf<Int, Long>()

        return getChildCount(0, sortedAdapters, childrenCountMap)
    }

    private fun getChildCount(index: Int, sortedAdapters: List<Int>, childrenCountMap: MutableMap<Int, Long>): Long {
        if (childrenCountMap.containsKey(index)) {
            return childrenCountMap[index]!!
        }
        return if (index == sortedAdapters.size - 1) {
            1
        } else {
            val adapter = sortedAdapters[index]
            sortedAdapters.drop(index + 1).filter { it <= adapter + 3 }.mapIndexed { i, _ ->
                val sum = getChildCount(index + 1 + i, sortedAdapters, childrenCountMap)
                childrenCountMap[index + 1 + i] = sum
                sum
            }.sum()
        }
    }
}