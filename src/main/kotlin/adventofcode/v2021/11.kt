package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {
        val octopuses = parseInput(FileParser.getFileRows(2021, "11.txt"))

        val time1 = measureTimeMillis {
            var flashcount = 0
            var step = 1
            while (true) {
                octopuses.forEach { octopuses[it.key] = octopuses[it.key]!! + 1 }

                val hasFlashed = mutableSetOf<Pair<Int, Int>>()
                do {
                    val flashers = octopuses
                        .filter { !hasFlashed.contains(it.key) }
                        .filter { it.value > 9 }

                    flashers.forEach { hasFlashed.add(it.key) }

                    flashers.forEach { flasher ->
                        AdventOfCodeUtil.getNeighbours2d(flasher.key)
                            .filter { !hasFlashed.contains(it) }
                            .forEach { pos -> octopuses.computeIfPresent(pos) { _, value -> value + 1 } }
                    }
                    flashcount += flashers.size
                } while (flashers.isNotEmpty())

                octopuses.filter { it.value > 9 }.forEach { octopuses[it.key] = 0 }

                if (step == 100) {
                    println("answer part 1: $flashcount")
                }
                if (octopuses.all { it.value == 0 }) {
                    println("answer part 2: $step")
                    break
                }
                step++
            }
        }
        println("Time: $time1 ms")
    }

    private fun parseInput(rows: List<String>): MutableMap<Pair<Int, Int>, Int> {
        var y = -1
        return rows.flatMap { row ->
            var x = 0
            y++
            row.toList().map { Pair(Pair(x++, y), it.digitToInt()) }
        }.toMap().toMutableMap()
    }
}