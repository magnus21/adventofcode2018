package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer

fun main(args: Array<String>) {

    val input = FileParser.getCommaSeparatedValuesAsList(2019, "2.txt").map { Integer.valueOf(it) }

    // Run program.
    val result = IntCodeComputer.runWithNounAndVerb(input.toMutableList(), 12, 2)
    println(result)


    // Find noun and verb for output 19690720.
    val pair = Day2.findInputForOutput(input.toMutableList(), 19690720)
    println(pair.first * 100 + pair.second)

}

object Day2 {

    fun findInputForOutput(program: MutableList<Int>, output: Int): Pair<Int, Int> {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val result = IntCodeComputer.runWithNounAndVerb(program.toMutableList(), noun, verb)
                if (result == output) return Pair(noun, verb)
            }
        }
        return Pair(-1, -1)
    }
}