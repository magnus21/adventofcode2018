package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.math.max
import kotlin.system.measureTimeMillis

object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {

        val ventLines = parseInput(FileParser.getFileRows(2021, "5.txt"))

        val time = measureTimeMillis {
            val answer1 = ventLines
                .filter { it.step.x == 0 || it.step.y == 0 }
                .flatMap { it.positions }
                .groupBy { it }
                .filter { it.value.size > 1 }
                .count()

            println("answer part 1: $answer1")

            val answer2 = ventLines
                .flatMap { it.positions }
                .groupBy { it }
                .filter { it.value.size > 1 }
                .count()

            println("answer part 2: $answer2")
        }
        println("Time: $time ms")
    }

    private fun parseInput(fileRows: List<String>): List<VentLine> {
        return fileRows
            .map { row ->
                val values = row.split("->", ",").map { it.trim() }.map(Integer::valueOf)

                // Only horizontal/vertical/45 degrees.
                val xStep = values[2] - values[0]
                val yStep = values[3] - values[1]
                val maxStep = abs(max(xStep, yStep))
                val step = VentPosition(xStep / maxStep, yStep / maxStep)

                var x = values[0]
                var y = values[1]
                val positions = mutableListOf(VentPosition(x, y))
                do {
                    x += step.x
                    y += step.y
                    positions.add(VentPosition(x, y))
                } while (x != values[2] || y != values[3])

                VentLine(step, positions)
            }
    }

    data class VentPosition(val x: Int, val y: Int)
    data class VentLine(
        val step: VentPosition,
        val positions: List<VentPosition>
    )
}