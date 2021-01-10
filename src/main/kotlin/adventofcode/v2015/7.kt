package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2015, "7.txt")


        val time1 = measureTimeMillis {
            val instructions = parseInput(input)
            val signals = mutableMapOf<String, UShort>()

            instructions.keys.forEach { outputWire ->
                evaluate(outputWire, signals, instructions)
            }
            println("Part 1: ${signals["a"]}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            val instructions = parseInput(input).toMutableMap()
            val signals = mutableMapOf<String, UShort>()

            instructions["b"] = Instruction({ a, _ -> a }, listOf("956"), "b")

            instructions.keys.forEach { outputWire ->
                evaluate(outputWire, signals, instructions)
            }
            println("Part 2: ${signals["a"]}")
        }
        println("Time: $time2 ms")
    }

    data class Instruction(
        val operation: (UShort, UShort) -> UShort,
        val inputWires: List<String>,
        val outputWire: String
    )

    private fun isNumber(input: String) = input.all { it.isDigit() }

    private fun evaluate(
        outputWire: String,
        signals: MutableMap<String, UShort>,
        instructions: Map<String, Instruction>
    ): UShort {
        if (signals.containsKey(outputWire)) {
            return signals[outputWire]!!
        }
        val ins = instructions[outputWire] ?: error("Instruction not found for: $outputWire")

        val signal = ins.operation(
            if (isNumber(ins.inputWires[0])) ins.inputWires[0].toUShort() else evaluate(
                ins.inputWires[0],
                signals,
                instructions
            ),
            when {
                ins.inputWires.size == 1 -> 0u
                isNumber(ins.inputWires[1]) -> ins.inputWires[1].toUShort()
                else -> evaluate(ins.inputWires[1], signals, instructions)
            }
        )
        signals[outputWire] = signal
        return signal
    }

    private fun parseInput(input: List<String>): Map<String, Instruction> {
        return input.map { row ->
            val parts = row.split(" -> ").map { it.trim() }

            val subParts = parts[0].split(' ')
            val value = when {
                parts[0].contains("AND") -> Instruction(
                    { a, b -> a and b }, listOf(subParts[0], subParts[2]), parts[1]
                )
                parts[0].contains("OR") -> Instruction(
                    { a, b -> a or b }, listOf(subParts[0], subParts[2]), parts[1]
                )
                parts[0].contains("LSHIFT") -> Instruction(
                    { a, b -> (a.toInt() shl b.toInt()).toUShort() }, listOf(subParts[0], subParts[2]), parts[1]
                )
                parts[0].contains("RSHIFT") -> Instruction(
                    { a, b -> (a.toInt() shr b.toInt()).toUShort() }, listOf(subParts[0], subParts[2]), parts[1]
                )
                parts[0].contains("NOT") -> Instruction(
                    { a, _ -> UShort.MAX_VALUE xor a }, listOf(subParts[1]), parts[1]
                )
                else -> Instruction({ a, _ -> a }, listOf(parts[0]), parts[1])
            }
            Pair(value.outputWire, value)
        }.toMap()
    }
}