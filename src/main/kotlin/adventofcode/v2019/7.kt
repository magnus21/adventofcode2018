package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.AdventOfCodeUtil
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "7.txt").map { it.toLong() }

        // Part 1
        val time1 = measureTimeMillis {
            val phaseSettings = AdventOfCodeUtil.generatePermutations(listOf(0L, 1, 2, 3, 4),5)

            val outputSignals = mutableMapOf<Long, List<Long>>()
            for (phaseSetting in phaseSettings) {

                var outputSignal = 0L
                for (i in 0..4) {
                    val result =
                        IntCodeComputer(input.toMutableList()).runWithInput(listOf(phaseSetting[i], outputSignal))
                    outputSignal = result.first[result.first.size - 1]
                }
                outputSignals[outputSignal] = phaseSetting
            }

            val maxOutputSignal = outputSignals.map { it.key }.maxOrNull()!!
            println("Max output signal, part 1: $maxOutputSignal")
        }
        println("Time part 1: ($time1 milliseconds)")


        // Part 2
        val time2 = measureTimeMillis {
            val phaseSettings = AdventOfCodeUtil.generatePermutations(listOf(5L, 6, 7, 8, 9),5)

            val outputSignals = mutableMapOf<Long, List<Long>>()
            for (phaseSetting in phaseSettings) {

                var outputSignal = 0L
                var counter = 0
                val computers = (0..4).map { IntCodeComputer(input.toMutableList()) }

                while (true) {
                    var result: Pair<MutableList<Long>, Int> = Pair(mutableListOf(), 0)
                    for (i in 0..4) {
                        val inputList =
                            if (counter == 0) listOf(phaseSetting[i], outputSignal) else listOf(outputSignal)

                        result = computers[i].runWithInput(inputList)

                        outputSignal = result.first[result.first.size - 1]
                    }

                    if (result.second == IntCodeComputer.DONE) {
                        outputSignals[outputSignal] = phaseSetting
                        break
                    }
                    counter++
                }
            }

            val maxOutputSignal = outputSignals.maxOf { it.key }
            println("Max output signal, part 2: $maxOutputSignal")
        }
        println("Time part 2: ($time2 milliseconds)")


    }

    private fun generatePhaseSettingsFrom(
        list: List<Long>,
        result: MutableList<List<Long>> = mutableListOf(),
        permutation: List<Long> = listOf()
    ): List<List<Long>> {

        for (i in 0 until list.size) {
            if (permutation.size == 4) {
                result.add(permutation.plusElement(list[i]))
                break
            }

            val listCopy = list.toMutableList()
            listCopy.removeAt(i)
            generatePhaseSettingsFrom(listCopy, result, permutation.plusElement(list[i]))
        }

        return result
    }
}