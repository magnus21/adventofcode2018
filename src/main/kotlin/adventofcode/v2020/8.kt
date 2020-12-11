package adventofcode.v2020

import adventofcode.util.FileParser
import adventofcode.v2020.Day8.Operation.*
import kotlin.system.measureTimeMillis

object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "8.txt")

        // Run program.
        val time = measureTimeMillis {
            val instructions = parseInput(input)

            println("Part 1: ${part1(instructions)}")
            println("Part 2: ${part2(instructions)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(instructions: Map<Int, Instruction>): Int {
        return runInstructions(instructions).first
    }

    private fun part2(instructions: Map<Int, Instruction>): Int {

        instructions.entries.filter { it.value.isNopOrJmp() }.forEach {
            val ins = instructions.toMutableMap();
            ins[it.key] = Instruction(it.value.operation.switchARoo(), it.value.argument)

            val result = runInstructions(ins)
            if (result.second) {
                return result.first
            }
        }
        return -999
    }


    private fun runInstructions(instructions: Map<Int, Instruction>): Pair<Int, Boolean> {
        var acc = 0
        var pointer = 0
        val visitedInstructions = mutableSetOf<Int>()
        while (true) {
            val ins = instructions[pointer]!!
            visitedInstructions.add(pointer)

            when (ins.operation) {
                Operation.acc -> {
                    acc += ins.argument
                    pointer++
                }
                jmp -> pointer += ins.argument
                else -> pointer++
            }

            if (visitedInstructions.contains(pointer)) {
                return Pair(acc, false)
            } else if (pointer >= instructions.size) {
                return Pair(acc, true)
            }
        }
    }


    private fun parseInput(input: List<String>): Map<Int, Instruction> {
        return input.mapIndexed { i, it ->
            val parts = it.split(' ')
            Pair(i, Instruction(valueOf(parts[0]), parts[1].toInt()))
        }.toMap()
    }

    enum class Operation {
        nop, acc, jmp;

        fun switchARoo(): Operation {
            return when (this) {
                nop -> jmp
                else -> nop
            }
        }
    }

    private data class Instruction(val operation: Operation, val argument: Int) {
        fun isNopOrJmp(): Boolean {
            return operation == nop || operation == jmp
        }
    }
}