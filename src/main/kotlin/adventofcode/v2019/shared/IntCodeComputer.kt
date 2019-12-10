package adventofcode.v2019.shared

import adventofcode.v2019.shared.IntCodeComputer.ParameterMode.*

class IntCodeComputer(var program: MutableList<Long>, var instructionPointer: Int = 0, var relativeBase: Int = 0) {

    companion object {
        const val WAITING_FOR_INPUT = 1;
        const val DONE = -1;
    }

    private var outputBuffer = mutableListOf<Long>()

    fun runWithNounAndVerb(noun: Int, verb: Int): Pair<MutableList<Long>, Int> {
        program[1] = noun.toLong()
        program[2] = verb.toLong()

        return runProgram()
    }

    fun runWithInput(input: List<Long>): Pair<MutableList<Long>, Int> {
        return runProgram(input)
    }

    private fun runProgram(input: List<Long>? = null): Pair<MutableList<Long>, Int> {
        var inputPointer = 0

        while (instructionPointer < program.size) {
            val opCode = getOpCode(instructionPointer)
            if (opCode == 99) {
                break
            }
            if (!listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 99).contains(opCode)) {
                throw Exception("Unknown op code: $opCode")
            }

            when (opCode) {
                1 -> add()
                2 -> multiply()
                3 -> {
                    if (input == null || inputPointer > input.size - 1) {
                        return Pair(outputBuffer, WAITING_FOR_INPUT)
                    }
                    input(input[inputPointer])
                    inputPointer++
                }
                4 -> output()
                5 -> jumpIfTrue()
                6 -> jumpIfFalse()
                7 -> lessThan()
                8 -> eq()
                9 -> adjustRelativeBase()
            }
        }

        return Pair(outputBuffer, DONE)
    }

    /************ START OPS  **********/
    private fun add() {

        val value1 = getParameterValue(1)
        val value2 = getParameterValue(2)

        setParameterValue(value1 + value2, 3)

        moveInstructionPointer(4)
    }

    private fun multiply() {
        val value1 = getParameterValue(1)
        val value2 = getParameterValue(2)

        setParameterValue(value1 * value2, 3)

        moveInstructionPointer(4)
    }

    private fun input(input: Long) {
        setParameterValue(input, 1)

        moveInstructionPointer(2)
    }

    private fun output() {
        val value = getParameterValue(1)
        //println("Program output: $value")
        outputBuffer.add(value)

        moveInstructionPointer(2)
    }

    private fun jumpIfTrue() {
        jump() { it != 0L }
    }

    private fun jumpIfFalse() {
        jump() { it == 0L }
    }

    private fun jump(predicate: (Long) -> Boolean) {
        val value1 = getParameterValue(1)

        if (predicate.invoke(value1)) {
            val value2 = getParameterValue(2)
            setInstructionPointerTo(value2.toInt())
        } else {
            moveInstructionPointer(3)
        }
    }

    private fun lessThan() {
        val value1 = getParameterValue(1)
        val value2 = getParameterValue(2)

        setParameterValue(if (value1 < value2) 1 else 0, 3)

        moveInstructionPointer(4)
    }

    private fun eq() {
        val value1 = getParameterValue(1)
        val value2 = getParameterValue(2)

        setParameterValue(if (value1 == value2) 1 else 0, 3)

        moveInstructionPointer(4)
    }


    private fun adjustRelativeBase() {
        relativeBase += getParameterValue(1).toInt()
        moveInstructionPointer(2)
    }

    /************ END OPS  **********/

    private fun getParameterValue(nr: Int): Long {
        val address = getAddress(nr)
        checkMemory(address)

        return program[address]
    }

    private fun setParameterValue(value: Long, nr: Int) {
        val address = getAddress(nr)
        checkMemory(address)

        program[address] = value
    }

    private fun getAddress(nr: Int): Int {
        val mode = getParameterMode(nr)

        return when (mode) {
            POSITION -> program[instructionPointer + nr].toInt()
            IMMEDIATE -> instructionPointer + nr
            else -> program[instructionPointer + nr].toInt() + relativeBase
        }
    }

    private fun checkMemory(address: Int) {
        if (address >= program.size) {
            expandMemory(address)
        }
    }

    private fun expandMemory(address: Int) {
        for (i in 1..(address - (program.size - 1))) {
            program.add(0)
        }
    }

    private fun moveInstructionPointer(offset: Int) {
        instructionPointer += offset
    }

    private fun setInstructionPointerTo(position: Int) {
        instructionPointer = position
    }

    private fun getOpCode(instructionPointer: Int): Int {
        val code = program[instructionPointer].toInt()
        return if (code < 10) code else code % 100
    }

    private fun getParameterMode(nr: Int): ParameterMode {
        val modes = (program[instructionPointer] / 100).toString().toCharArray()
            .reversed()
            .map(Char::toString)
            .map { Integer.valueOf(it) }

        return if (modes.size < nr || modes[nr - 1] == 0) POSITION else if (modes[nr - 1] == 1) IMMEDIATE else RELATIVE
    }

    private enum class ParameterMode {
        POSITION,
        IMMEDIATE,
        RELATIVE
    }
}