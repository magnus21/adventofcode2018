package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {

        val depths = FileParser.getFileRows(2021, "1.txt").map { Integer.valueOf(it) }

        val time1 = measureTimeMillis {
            val nrIncreases = getIncreases(depths)
            println("answer part 1: $nrIncreases")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val windowSums = depths.fold(Pair(mutableListOf<Int>(), mutableListOf<Int>())) { acc, depth ->
                acc.first.add(depth)
                if (acc.first.size < 3) {
                    acc
                } else {
                    acc.second.add(acc.first.sum())
                    Pair(acc.first.drop(1).toMutableList(), acc.second)
                }
            }.second
            println("answer part 2: ${getIncreases(windowSums)}")
        }
        println("Time: $time2 ms")
    }

    private fun getIncreases(depths: List<Int>) =
        depths.fold(Pair(0, 0)) { acc, d ->
            if (acc.first == 0) Pair(d, 0)
            else Pair(d, if (d > acc.first) acc.second + 1 else acc.second)
        }.second
}