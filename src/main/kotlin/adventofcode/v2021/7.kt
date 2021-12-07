package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val positions = FileParser.getFileRows(2021, "7.txt")
            .flatMap { it.split(",") }
            .map(Integer::valueOf)

        val time1 = measureTimeMillis {
            val fuelCost = getNeededFuel(positions) { it }
            println("answer part 1: $fuelCost")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val fuelCost = getNeededFuel(positions) { distance: Int -> distance * (distance + 1) / 2 }
            println("answer part 2: $fuelCost")
        }
        println("Time: $time2 ms")
    }

    private fun getNeededFuel(positions: List<Int>, fuelFormula: (Int) -> Int) =
        (positions.minOrNull()!!..positions.maxOrNull()!!)
            .map { pos -> Pair(pos, positions.sumOf { fuelFormula(abs(it - pos)) }) }
            .minByOrNull { it.second }
            ?.second!!
}