package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer

fun main(args: Array<String>) {

    val input = FileParser.getCommaSeparatedValuesAsList(2019, "2.txt").map { it.toLong() }

    // Run program.
    val program = input.toMutableList()
    IntCodeComputer(program).runWithNounAndVerb(12, 2)
    println(program[0])


    // Find noun and verb for output 19690720.
    val pair = Day2.findInputForOutput(input.toMutableList(), 19690720)
    println(pair.first * 100 + pair.second)
}

object Day2 {

    fun findInputForOutput(program: MutableList<Long>, output: Long): Pair<Int, Int> {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val code = program.toMutableList()
                val result = IntCodeComputer(code).runWithNounAndVerb(noun, verb)
                if (code[0] == output) {
                    return Pair(noun, verb)
                }
            }
        }
        return Pair(-1, -1)
    }
}