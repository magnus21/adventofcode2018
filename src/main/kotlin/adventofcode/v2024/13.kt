package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day13 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        printResult("part 1") { solve(0.0) }
        printResult("part 1") { solve(10000000000000.0) }
    }

    private fun solve(pricePositionAddition: Double): Double {
        /**
         * a*Ax + b*Bx = priceX
         * a*Ay + b*By = priceY
         *
         * a = (priceX - b*Bx)/Ax
         *
         *
         * b = (Ax*priceY - Ay*priceX)/(Ax*By - Ay*Bx)
         *
         */
        val clawMachines = parseInput(FileParser.getFileRows(2024, "13.txt"), pricePositionAddition)

        return clawMachines.map { cm ->
            val bCount =
                (cm.buttonASteps.x * cm.prizeLocation.y - cm.buttonASteps.y * cm.prizeLocation.x) / (cm.buttonASteps.x * cm.buttonBSteps.y - cm.buttonASteps.y * cm.buttonBSteps.x)
            val aCount = (cm.prizeLocation.x - bCount * cm.buttonBSteps.x) / cm.buttonASteps.x

            DecimalPoint(aCount, bCount)
        }.filter { it.x % 1 == 0.0 && it.y % 1 == 0.0 }
            .sumOf { it.x * 3 + it.y }
    }


    data class ClawMachine(
        val buttonASteps: DecimalPoint,
        val buttonBSteps: DecimalPoint,
        val prizeLocation: DecimalPoint
    )

    data class DecimalPoint(val x: Double, val y: Double)

    private fun parseInput(input: List<String>, pricePositionAddition: Double) =
        input.chunked(4).map { clawMachine ->
            val buttons = clawMachine.take(2).map { row ->
                "X\\+(\\d+), Y\\+(\\d+)".toRegex().findAll(row).map {
                    DecimalPoint(it.groups[1]!!.value.toDouble(), it.groups[2]!!.value.toDouble())
                }.first()
            }
            val prize = "X=(\\d+), Y=(\\d+)".toRegex().findAll(clawMachine.take(3).last()).map {
                DecimalPoint(
                    it.groups[1]!!.value.toDouble() + pricePositionAddition,
                    it.groups[2]!!.value.toDouble() + pricePositionAddition
                )
            }.first()
            ClawMachine(buttons[0], buttons[1], prize)
        }
}