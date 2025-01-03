package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.pow
import kotlin.time.ExperimentalTime

object Day17 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val (register, program) = parseInput(FileParser.getFileRows(2024, "17.txt"))

        printResult("part 1") { solve1(register.toMutableMap(), program) }
        printResult("part 2") { solve2(register.toMutableMap(), program) }
        //Correct: 108107566389757: 2413754113035530 (Semi brute force)
    }


    private fun solve1(register: MutableMap<String, Long>, program: List<Int>, part2: Boolean = false): String {
        var pointer = 0
        val outputs = mutableListOf<Int>()
        while (pointer + 1 < program.size) {
            val opCode = program[pointer]
            val operand = program[pointer + 1]
            val (output, pointerPosition) = executeInstruction(opCode, operand, register)
            if (output != null) {
                outputs.add(output)
                if (part2 && outputs != program.subList(0, outputs.size)) {
                    break
                }
            }
            pointer = pointerPosition ?: (pointer + 2)
        }

        return outputs.joinToString(if (part2) "" else ",")
    }

    private fun solve2(reg: MutableMap<String, Long>, program: List<Int>): Long {
        /*
        var a = reg["A"]!!
        var b = reg["B"]!!
        while (a != 0L) {
            b = (((a % 8) xor 3) xor a / 2.0.pow(b.toInt()).toInt()) xor 3
            // 0-7 xor 3 xor a/2^b xor 3
            a = a / 8
            print(b % 8)
        }
        */
        // 1. Start  by finding number that gives same output length => 35184372088832 28147498000000
        // 2
        val start = 68602457201149 // 35184372088832 //17592186542589 //8.0.pow(program.size).toLong()
        var i = start
        val programString = program.joinToString("")
        while (true) {
            reg["A"] = i
            val output = solve1(reg.toMutableMap(), program, true)
            if (output == programString) {
                println("Correct: $i: $output")
            } else if (output.length >= 7 && output.substring(0, 7) == programString.substring(0, 7)) {
                println("$i: $output")
                return i
            }
            i += 4194304 // Reoccuring diff with matching prefix of program.
            // i++
            // i=i*2
            // 17592186542589: Correct first numbers occur every 524288
        }
    }

    private fun parseInput(input: List<String>): Pair<Map<String, Long>, List<Int>> {
        val register = input.take(3).map {
            val parts = it.split(" ")
            Pair(parts[1].dropLast(1), parts[2].toLong())
        }.toMap()

        val program = input.last().drop(9).split(",").map { it.toInt() }

        return Pair(register, program)
    }

    private fun executeInstruction(opCode: Int, operand: Int, register: MutableMap<String, Long>): Pair<Int?, Int?> {
        var output: Int? = null
        var pointer: Int? = null

        when (opCode) {
            0 -> register["A"] = register["A"]!! / 2.0.pow(getComboOperand(operand, register).toInt()).toLong()
            1 -> register["B"] = register["B"]!! xor operand.toLong()
            2 -> register["B"] = getComboOperand(operand, register) % 8L
            3 -> if (register["A"] != 0L) pointer = operand
            4 -> register["B"] = register["B"]!! xor register["C"]!!
            5 -> output = (getComboOperand(operand, register) % 8).toInt()
            6 -> register["B"] = register["A"]!! / 2.0.pow(getComboOperand(operand, register).toInt()).toLong()
            7 -> register["C"] = register["A"]!! / 2.0.pow(getComboOperand(operand, register).toInt()).toLong()
        }

        return Pair(output, pointer)
    }

    private fun getComboOperand(operand: Int, register: Map<String, Long>): Long {
        return when (operand) {
            0, 1, 2, 3 -> operand.toLong()
            4 -> register["A"]!!
            5 -> register["B"]!!
            else -> register["C"]!!
        }
    }
}