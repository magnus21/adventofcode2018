package adventofcode.v2019

import adventofcode.util.FileParser

fun main(args: Array<String>) {

    val input = FileParser.getCommaSeparatedValuesAsList(2019, "2.txt").map { Integer.valueOf(it) }

    // Run program.
    val result = Day2.runProgram(input.toMutableList(), 12, 2)
    println(result)


    // Find noun and verb for output 19690720.
    val pair = Day2.findInputForOutput(input.toMutableList(), 19690720)
    println(pair.first * 100 + pair.second)

}

object Day2 {

    fun runProgram(program: MutableList<Int>, noun: Int, verb: Int): Int {

        program[1] = noun
        program[2] = verb

        var instructionPointer = 0
        while (instructionPointer < program.size) {
            val opCode = program[instructionPointer]

            if (opCode == 99) break
            if (!listOf(1, 2, 99).contains(opCode)) throw Exception("Unknown op code")

            val value1 = program[program[instructionPointer + 1]]
            val value2 = program[program[instructionPointer + 2]]

            program[program[instructionPointer + 3]] = when (opCode) {
                1 -> value1 + value2
                else -> value1 * value2
            }

            instructionPointer += 4
        }
        return program[0]
    }

    fun findInputForOutput(program: MutableList<Int>, output: Int): Pair<Int, Int> {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val result = runProgram(program.toMutableList(), noun, verb)
                if (result == output) return Pair(noun, verb)
            }
        }
        return Pair(-1, -1)
    }
}