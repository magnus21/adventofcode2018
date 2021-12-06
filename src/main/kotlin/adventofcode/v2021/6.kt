package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val lanternFishTimers = FileParser.getFileRows(2021, "6.txt")
            .flatMap { it.split(",") }
            .map(Integer::valueOf)

        val time1 = measureTimeMillis {
            println("answer part 1: ${getLanternFishesAfter(lanternFishTimers.toMutableList(), 80)}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            println("answer part 2: ${getLanternFishesAfter(lanternFishTimers.toMutableList(), 256)}")
        }
        println("Time: $time2 ms")
    }

    private fun getLanternFishesAfter(lanternFishTimers: MutableList<Int>, days: Int): Long {
        var timersMap = lanternFishTimers.groupingBy { it }.eachCount()
            .map { Pair(it.key, it.value.toLong()) }
            .toMap()
            .toMutableMap()

        (1..days).forEach {
            val map = mutableMapOf<Int, Long>()
            timersMap.keys.forEach { timer ->
                val timerCount = timersMap[timer]!!
                when (timer) {
                    0 -> {
                        map[6] = map.getOrDefault(6, 0) + timerCount
                        map[8] = timerCount
                    }
                    else -> map[timer - 1] = map.getOrDefault(timer - 1, 0) + timerCount
                }
            }
            timersMap = map
        }
        return timersMap.map { it.value }.sum()
    }
}