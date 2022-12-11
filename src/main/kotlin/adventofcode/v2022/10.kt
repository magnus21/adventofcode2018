package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day10 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val instructions = FileParser.getFileRows(2022, "10.txt")
            .map {
                val parts = it.split(" ")
                Pair(parts[0], if (parts.size == 2) parts[1].toInt() else 0)
            }

        printResult("part 1") { part1(instructions) }
        printResult("part 2") { part2(instructions) }
    }

    private fun part1(instructions: List<Pair<String, Int>>): Int {

        var state = 1
        val registerFinalStates = mutableListOf<Int>()
        instructions.forEach { ins ->
            if (ins.first != "noop") {
                registerFinalStates.add(state)
                state += ins.second
            }
            registerFinalStates.add(state)
        }
        // Find the signal strength during the 20th, 60th, 100th, 140th, 180th, and 220th cycles.
        return (20..220 step 40).sumOf { registerFinalStates[it - 2] * it }
    }

    private fun part2(instructions: List<Pair<String, Int>>): String {

        var state = 1
        val output = mutableListOf("\n")
        var position = 0
        instructions.forEach { ins ->
            output += getPixel(state, position)
            if (ins.first != "noop") {
                position = checkLineBreak(++position, output)
                output += getPixel(state, position)
                state += ins.second
            }
            position = checkLineBreak(++position, output)
        }
        return output.joinToString("")
    }

    private fun checkLineBreak(position: Int, output: MutableList<String>): Int {
        if (position == 40) {
            output.add("\n")
            return 0
        }
        return position
    }

    private fun getPixel(state: Int, position: Int): String {
        return if (state - 1 == position || state == position || state + 1 == position) "#" else " "
    }
}