package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "19.txt").map(String::toLong)

        // Run program.
        val time1 = measureTimeMillis {

            val map = mutableMapOf<Position, Int>()
            for (y in 0 until 50) {
                for (x in 0 until 50) {
                    val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf(x.toLong(), y.toLong()))
                        .first[0]
                    map[Position(x, y)] = result.toInt()
                }
            }

            printMap(map)

            val tractorBeamAffectedPositions = map.entries.filter { it.value == 1 }.count()

            println("Answer part 1: $tractorBeamAffectedPositions")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            // Good guess
            var xOffset = 1500
            var yOffset = 765

            while (true) {
                val map = mutableMapOf<Position, Int>()
                for (y in 0 + yOffset until 100 + yOffset) {
                    for (x in 0 + xOffset until 100 + xOffset) {
                        val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf(x.toLong(), y.toLong()))
                            .first[0]
                        map[Position(x, y)] = result.toInt()
                    }
                }

                val zerosInUpperCorner =
                    map.entries.filter { it.key.x - xOffset >= it.key.y - yOffset }.filter { it.value == 0 }.count()
                val zerosInLowerCorner =
                    map.entries.filter { it.key.x - xOffset < it.key.y - yOffset }.filter { it.value == 0 }.count()

                if (zerosInUpperCorner == 0 && zerosInLowerCorner == 0) {
                    break
                }

                val step = Math.max(
                    1,
                    Math.sqrt(zerosInUpperCorner.toDouble() + zerosInLowerCorner.toDouble() - 2).toInt() / 2 // To be safe..
                )

                if (zerosInUpperCorner > zerosInLowerCorner) {
                    yOffset += step
                } else {
                    xOffset += step
                }
                //printMap(map)
                println("Zero count: upper: $zerosInUpperCorner, lower: $zerosInLowerCorner")

                println("Offsets: $xOffset, $yOffset")
            }

            val result = xOffset * 10000 + yOffset
            //val result = 1509*10000 + 773

            println("Answer part 2: $result")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun printMap(map: MutableMap<Position, Int>) {

        val xSpan = Pair(map.keys.map { it.x }.min()!!, map.keys.map { it.x }.max()!!)
        val ySpan = Pair(map.keys.map { it.y }.min()!!, map.keys.map { it.y }.max()!!)

        for (y in ySpan.first..ySpan.second) {
            for (x in xSpan.first..xSpan.second) {
                val tile = map[Position(x, y)]
                print(tile!!)
            }
            println()
        }
    }

    private data class Position(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }
}