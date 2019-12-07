package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "7.txt").map { Integer.valueOf(it) }

        // Part 1
        val time1 = measureTimeMillis {
            val phaseSettings = generatePhaseSettingsFrom(listOf(0, 1, 2, 3, 4));

            val outputSignals = mutableMapOf<Int, List<Int>>()
            for (phaseSetting in phaseSettings) {

                var outputSignal = 0
                for (i in 0..4) {
                    val result =
                        IntCodeComputer(input.toMutableList()).runWithInput(listOf(phaseSetting[i], outputSignal))
                    outputSignal = result.first[result.first.size - 1]
                }
                outputSignals[outputSignal] = phaseSetting
            }

            val maxOutputSignal = outputSignals.map { it.key }.max()!!
            println("Max output signal, part 1: $maxOutputSignal")
        }
        println("Time part 1: ($time1 milliseconds)")


        // Part 2
        val time2 = measureTimeMillis {
            val phaseSettings = generatePhaseSettingsFrom(listOf(5, 6, 7, 8, 9));

            val outputSignals = mutableMapOf<Int, List<Int>>()
            for (phaseSetting in phaseSettings) {

                var outputSignal = 0
                var counter = 0
                val computers = (0..4).map { IntCodeComputer(input.toMutableList()) }

                while (true) {
                    var result: Pair<MutableList<Int>, Int> = Pair(mutableListOf(), 0)
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

            val maxOutputSignal = outputSignals.map { it.key }.max()!!
            println("Max output signal, part 2: $maxOutputSignal")
        }
        println("Time part 2: ($time2 milliseconds)")


    }

    private fun generatePhaseSettingsFrom(
        list: List<Int>,
        result: MutableList<List<Int>> = mutableListOf(),
        permutation: List<Int> = listOf()
    ): List<List<Int>> {

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