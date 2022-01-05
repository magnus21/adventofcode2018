package adventofcode.v2021

import adventofcode.util.FileParser
import adventofcode.v2021.Day24.InstructionType.*
import java.util.Optional.ofNullable
import kotlin.system.measureTimeMillis

object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {

        val time = measureTimeMillis {
            val instructions = parseInput(FileParser.getFileRows(2021, "24.txt")).toMutableList()

            val keyOpsMap = mutableMapOf<Int, Pair<Int, Int>>()
            var c = 0;
            for (i in 0 until instructions.size step 18) {
                keyOpsMap[c++] =
                    Pair(instructions[i + 5].params[1].toInt(), instructions[i + 15].params[1].toInt())
            }

            val possibleValidModelNumbers = getModelNumbers(0, "", 0, keyOpsMap)

            val answer1 = possibleValidModelNumbers.map { it.toLong() }.maxOrNull()
            // Might as well use "validateModelNumber" since I wrote it :)
            println("Validate 1: ${validateModelNumber(instructions, answer1.toString())}")
            println("answer part 1: $answer1")

            val answer2 = possibleValidModelNumbers.map { it.toLong() }.minOrNull()
            println("Validate 2: ${validateModelNumber(instructions, answer2.toString())}")
            println("answer part 2: $answer2")
        }
        println("Time: $time ms")
    }

    private fun getModelNumbers(
        inputNr: Int,
        modelNr: String,
        zAcc: Int,
        keyOpsMap: MutableMap<Int, Pair<Int, Int>>
    ): List<String> {
        val keyOpsParams = keyOpsMap[inputNr]!!

        // if first adds input > 10 all inputs possible we will increase z (x will be 1).
        if (keyOpsParams.first >= 10) {
            return (1..9).flatMap {
                val zOut = zAcc * 26 + (it + keyOpsParams.second)
                getModelNumbers(inputNr + 1, modelNr + it, zOut, keyOpsMap)
            }
        }

        // Else first adds input < 10 all inputs possible (z will decrease for one input only, might not even exists though).
        val inputNeededToNotIncreaseZ = zAcc % 26 + keyOpsParams.first
        if (inputNeededToNotIncreaseZ in (1..9)) {
            val zOut = zAcc / 26

            if (inputNr == 13) {
                return if (zOut == 0) listOf(modelNr + inputNeededToNotIncreaseZ) else emptyList()
            }

            return getModelNumbers(inputNr + 1, modelNr + inputNeededToNotIncreaseZ, zOut, keyOpsMap)
        }

        // Valid input needed to decrease z for all possibilities.
        return emptyList()
    }

    private fun validateModelNumber(instructions: List<Instruction>, modelNumber: String, startZ: Long = 0): Long {
        val vars = mutableMapOf(Pair('w', 0L), Pair('x', 0L), Pair('y', 0L), Pair('z', startZ))

        var inputIndex = 0
        instructions.forEach { ins ->
            when (ins.type) {
                inp -> {
                    vars[ins.params[0][0]] = modelNumber[inputIndex++].digitToInt().toLong()
                }
                add, mul, div, mod, eql -> {
                    val a = ins.params[0][0]
                    val b = ins.params[1]
                    vars[a] = ins.type.op(vars[a]!!, getValue(b, vars))
                }
            }
        }

        return vars['z']!!
    }

    private fun getValue(param: String, vars: MutableMap<Char, Long>): Long {
        return ofNullable(param.toLongOrNull()).orElseGet { vars[param[0]]!! }
    }

    private fun parseInput(rows: List<String>): List<Instruction> {
        return rows.map {
            val parts = it.split(" ")
            Instruction(InstructionType.valueOf(parts[0]), parts.drop(1))
        }
    }

    data class Instruction(val type: InstructionType, val params: List<String>)
    enum class InstructionType(val op: (a: Long, b: Long) -> Long) {
        inp({ a, b -> -1 }),
        add({ a, b -> a + b }),
        mul({ a, b -> a * b }),
        div({ a, b -> a / b }),
        mod({ a, b -> a % b }),
        eql({ a, b -> if (a == b) 1 else 0 })
    }
}