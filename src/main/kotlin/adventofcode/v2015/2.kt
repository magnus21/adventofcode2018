package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.math.min
import kotlin.system.measureTimeMillis

object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {

        val dimensions = FileParser.getFileRows(2015, "2.txt").map {
            val parts = it.split("x").map { d -> d.toInt() }
            Triple(parts[0], parts[1], parts[2])
        }

        val time1 = measureTimeMillis {
            val answerPart1 = dimensions
                .map {
                    val a1 = it.first * it.second
                    val a2 = it.second * it.third
                    val a3 = it.third * it.first

                    2 * a1 + 2 * a2 + 2 * a3 + min(min(a1, a2), a3)
                }.sum()
            println("Part 1: $answerPart1")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val answerPart2 = dimensions
                .map {
                    val bow = it.first * it.second *it.third
                    val orderedBySize = it.toList().sorted()

                    orderedBySize[0]*2 + orderedBySize[1]*2 + bow
                }.sum()

            println("Part 2: $answerPart2")
        }
        println("Time: $time2 ms")
    }
}