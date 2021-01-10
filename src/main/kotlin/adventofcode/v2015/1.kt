package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {

        val entries = FileParser.getFileRows(2015, "1.txt")

        val time1 = measureTimeMillis {
            val answerPart1 = entries[0].fold(0) { floor, c -> if (c == '(') floor + 1 else floor - 1 }
            println("Part 1: $answerPart1")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val answerPart2 = entries[0].foldIndexed(listOf(Pair(0, 1))) { i, floors, c ->
                floors.plus(Pair(floors.last().first + (if (c == '(') 1 else -1), i + 1))
            }.first { it.first == -1 }.second

            println("Part 2: $answerPart2")
        }
        println("Time: $time2 ms")
    }
}