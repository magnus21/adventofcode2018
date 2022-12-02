package adventofcode.v2022

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {
        val inputs = FileParser.getFileRows(2022, "1.txt")

        val time1 = measureTimeMillis {
            val answer = getCalorieSums(inputs).maxByOrNull { it }
            println("answer part 1: $answer")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val answer = getCalorieSums(inputs).sortedByDescending { it }.take(3).sum()
            println("answer part 2: $answer")
        }
        println("Time: $time2 ms")
    }

    private fun getCalorieSums(inputs: List<String>): List<Int> {
        return inputs.fold(Pair(0, listOf<Int>())) { acc, calorie ->
            when {
                calorie.isEmpty() -> Pair(0, acc.second.plus(acc.first))
                else -> Pair(acc.first + calorie.toInt(), acc.second)
            }
        }.second
    }
}