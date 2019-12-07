package adventofcode.v2019.shared

import adventofcode.v2019.shared.IntCodeComputer.ParameterMode.IMMEDIATE
import adventofcode.v2019.shared.IntCodeComputer.ParameterMode.POSITION

class IntCodeComputer(var program: MutableList<Int>, var instructionPointer: Int = 0) {

    companion object {
        public val WAITING_FOR_INPUT = 1;
        public val DONE = -1;
    }

    private var outputBuffer =  mutableListOf<Int>()

    fun runWithNounAndVerb(noun: Int, verb: Int): Pair<MutableList<Int>, Int> {
        program[1] = noun
        program[2] = verb

        return runProgram()
    }

    fun runWithInput(input: List<Int>): Pair<MutableList<Int>, Int> {
        return runProgram(input)
    }

    private fun runProgram(input: List<Int>? = null): Pair<MutableList<Int>, Int> {
        var inputPointer = 0

        while (instructionPointer < program.size) {
            val opCode = getOpCode(program, instructionPointer)
            if (opCode == 99) {
                break
            }
            if (!listOf(1, 2, 3, 4, 5, 6, 7, 8, 99).contains(opCode)) {
                throw Exception("Unknown op code: $opCode")
            }

            when (opCode) {
                1 -> add(program)
                2 -> multiply(program)
                3 -> {
                    if (input == null || inputPointer > input.size - 1) {
                        // Waiting for input
                        return Pair(outputBuffer, WAITING_FOR_INPUT)
                    }
                    input(program, input[inputPointer])
                    inputPointer++
                }
                4 -> output(program)
                5 -> jumpIfTrue(program)
                6 -> jumpIfFalse(program)
                7 -> lessThan(program)
                else -> eq(program)
            }
        }

        return Pair(outputBuffer, DONE)
    }

    private fun add(program: MutableList<Int>) {

        val value1 = getParameterValue(program, 1)
        val value2 = getParameterValue(program, 2)

        setParameterValue(program, value1 + value2, 3)

        moveInstructionPointer(4)
    }

    private fun multiply(program: MutableList<Int>) {
        val value1 = getParameterValue(program, 1)
        val value2 = getParameterValue(program, 2)

        setParameterValue(program, value1 * value2, 3)

        moveInstructionPointer(4)
    }

    private fun input(program: MutableList<Int>, input: Int) {
        setParameterValue(program, input, 1)

        moveInstructionPointer(2)
    }

    private fun output(program: MutableList<Int>) {
        val value = getParameterValue(program, 1)
        //println("Program output: $value")
        outputBuffer.add(value)

        moveInstructionPointer(2)
    }

    private fun jumpIfTrue(program: MutableList<Int>) {
        jump(program) { it != 0 }
    }

    private fun jumpIfFalse(program: MutableList<Int>) {
        jump(program) { it == 0 }
    }

    private fun jump(program: MutableList<Int>, predicate: (Int) -> Boolean) {
        val value1 = getParameterValue(program, 1)

        if (predicate.invoke(value1)) {
            val value2 = getParameterValue(program, 2)
            setInstructionPointerTo(value2)
        } else {
            moveInstructionPointer(3)
        }
    }

    private fun lessThan(program: MutableList<Int>) {
        val value1 = getParameterValue(program, 1)
        val value2 = getParameterValue(program, 2)

        setParameterValue(program, if (value1 < value2) 1 else 0, 3)

        moveInstructionPointer(4)
    }

    private fun eq(program: MutableList<Int>) {
        val value1 = getParameterValue(program, 1)
        val value2 = getParameterValue(program, 2)

        setParameterValue(program, if (value1 == value2) 1 else 0, 3)

        moveInstructionPointer(4)
    }

    private fun getParameterValue(program: MutableList<Int>, nr: Int): Int {
        return if (getParameterMode(program, nr) == POSITION) program[program[instructionPointer + nr]]
        else program[instructionPointer + nr]
    }

    private fun setParameterValue(program: MutableList<Int>, value: Int, nr: Int) {
        program[program[instructionPointer + nr]] = value
    }

    private fun moveInstructionPointer(offset: Int) {
        instructionPointer += offset
    }

    private fun setInstructionPointerTo(position: Int) {
        instructionPointer = position
    }

    private fun getOpCode(program: MutableList<Int>, instructionPointer: Int): Int {
        val code = program[instructionPointer]
        return if (code < 10) code else code % 100
    }

    private fun getParameterMode(program: MutableList<Int>, nr: Int): ParameterMode {
        val modes = (program[instructionPointer] / 100).toString().toCharArray()
            .reversed()
            .map(Char::toString)
            .map { Integer.valueOf(it) }

        return if (modes.size < nr || modes[nr - 1] == 0) POSITION else IMMEDIATE
    }

    private enum class ParameterMode {
        POSITION,
        IMMEDIATE
    }
}