package adventofcode.v2019.shared

import adventofcode.v2019.shared.IntCodeComputer.ParameterMode.IMMEDIATE
import adventofcode.v2019.shared.IntCodeComputer.ParameterMode.POSITION

object IntCodeComputer {

    var instructionPointer = 0

    fun runWithNounAndVerb(program: MutableList<Int>, noun: Int, verb: Int, outputEndState: Boolean = false): Int {
        program[1] = noun
        program[2] = verb

        return runProgram(program, outputEndState = outputEndState)
    }

    fun runWithInput(program: MutableList<Int>, input: Int, outputEndState: Boolean = false): Int {
        return runProgram(program, input, outputEndState = outputEndState)
    }

    private fun runProgram(program: MutableList<Int>, input: Int? = null, outputEndState: Boolean = false): Int {
        instructionPointer = 0

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
                3 -> input(program, input!!)
                4 -> output(program)
                5 -> jumpIfTrue(program)
                6 -> jumpIfFalse(program)
                7 -> lessThan(program)
                else -> equalOp(program)
            }
        }

        if (outputEndState) {
            println("Program end state: $program")
        }

        return program[0]
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
        println("Program output: ${getParameterValue(program, 1)}")

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

    private fun equalOp(program: MutableList<Int>) {
        val value1 = getParameterValue(program, 1)
        val value2 = getParameterValue(program, 2)

        setParameterValue(program, if (value1 == value2) 1 else 0, 3)

        moveInstructionPointer(4)
    }

    private fun getParameterValue(program: MutableList<Int>, nr: Int): Int {
        val parameterModes = getParameterModes(program, instructionPointer)

        return if (getParameterMode(parameterModes, nr) == POSITION) program[program[instructionPointer + nr]]
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

    private fun getParameterModes(program: MutableList<Int>, instructionPointer: Int): List<Int> {
        return (program[instructionPointer] / 100).toString().toCharArray()
            .reversed()
            .map(Char::toString)
            .map { Integer.valueOf(it) }
    }

    private fun getParameterMode(modes: List<Int>, nr: Int): ParameterMode {
        return if (modes.size < nr || modes[nr - 1] == 0) POSITION else IMMEDIATE
    }

    private enum class ParameterMode {
        POSITION,
        IMMEDIATE
    }
}