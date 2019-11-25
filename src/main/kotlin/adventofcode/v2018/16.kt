package adventofcode.v2018

import java.io.File

fun main(args: Array<String>) {

    val operationCodes = OperationCodes()

    val (operationResults, instructions) = operationCodes.parseInput()

    //println(operationResults)
    //println(registries)

    val operations = operationCodes.getOperations()

    val partOneAnswer = operationResults.map { operationResult ->
        val ins = operationResult.instruction
        operations.filter { operation ->
            val registry = operationResult.beforeState.toMutableList()
            operation.value(ins.a, ins.b, ins.c, registry)
            registry == operationResult.afterState
        }.size
    }.filter { it >= 3 }.size

    println(partOneAnswer)

    val opCodeMap = operationCodes.getOpCodes(operationResults, operations)

    println(opCodeMap)

    val registry = mutableListOf(0L, 0L, 0L, 0L)
    for (ins in instructions) {
        val operation = operations[opCodeMap.filter { it.first == ins.opCode }.map { it.second }.first()]
        operation?.invoke(ins.a, ins.b, ins.c, registry)
    }

    println(registry)
}

data class OperationResult(var beforeState: List<Long>, var afterState: List<Long>, var instruction: Instruction)
data class Instruction(val opCode: Long, val a: Long, val b: Long, val c: Long)

class OperationCodes {

    fun parseInput(): Pair<List<OperationResult>, List<Instruction>> {

        val rawInput = File("src/main/resources/16.txt").readLines()

        val operations = mutableListOf<OperationResult>()
        val instructions = mutableListOf<Instruction>()

        var count = 0
        while (rawInput[count].isNotEmpty()) {

            val before = parseRegistry(rawInput[count])
            val opcode = parseInstruction(rawInput[count + 1])
            val after = parseRegistry(rawInput[count + 2])

            operations.add(OperationResult(before, after, opcode))

            count += 4
        }

        while (count++ < rawInput.size - 1) {
            if (rawInput[count].isBlank()) {
                continue
            }

            instructions.add(parseInstruction(rawInput[count]))
        }

        return Pair(operations, instructions)
    }

    fun parseInstruction(input: String): Instruction {
        val list = input.split(" ").map { Integer.valueOf(it).toLong() }.toList()
        return Instruction(list[0], list[1], list[2], list[3])
    }

    fun parseRegistry(input: String): List<Long> {
        return input.substring(9, input.length - 1).split(", ").map { Integer.valueOf(it).toLong() }.toList()
    }

    fun getOpCodes(
        operationResults: List<OperationResult>,
        operations: MutableMap<String, (Long, Long, Long, MutableList<Long>) -> Unit>
    ): List<Pair<Long, String>> {
        val opCodes = operationResults.map { it.instruction.opCode }.distinct().sorted()

        var operationsMatched = opCodes.map { opCode ->
            val matchingOperationsResults = operationResults
                .filter { it.instruction.opCode == opCode }

            val matchingOperations = operations.filter { operation ->
                matchingOperationsResults.all { operationResult ->
                    val ins = operationResult.instruction
                    val registry = operationResult.beforeState.toMutableList()
                    operation.value(ins.a, ins.b, ins.c, registry)
                    registry == operationResult.afterState
                }
            }

            Pair(opCode, matchingOperations.map { it.key })
        }

        // Reduce
        while (operationsMatched.any { it.second.size > 1 }) {
            val oneMatches = operationsMatched
                .filter { it.second.size == 1 }.toMutableList()

            val oneOps = operationsMatched
                .filter { it.second.size == 1 }
                .flatMap { it.second }

            val reduced = operationsMatched
                .filter { it.second.size > 1 }
                .map { Pair(it.first, it.second.filter { op -> !oneOps.contains(op) }) }

            oneMatches.addAll(reduced)
            operationsMatched = oneMatches
        }

        return operationsMatched.map { Pair(it.first, it.second[0]) }
    }

    fun getOperations(): MutableMap<String, (Long, Long, Long, MutableList<Long>) -> Unit> {
        val operations = mutableMapOf<String, (Long, Long, Long, MutableList<Long>) -> Unit>()

        operations["addr"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()] + registry[b.toInt()] }
        operations["addi"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()] + b }

        operations["mulr"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()] * registry[b.toInt()] }
        operations["muli"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()] * b }

        operations["banr"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()].and(registry[b.toInt()]) }
        operations["bani"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()].and(b) }

        operations["borr"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()].or(registry[b.toInt()]) }
        operations["bori"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()].or(b) }

        operations["setr"] = { a, b, c, registry -> registry[c.toInt()] = registry[a.toInt()] }
        operations["seti"] = { a, b, c, registry -> registry[c.toInt()] = a }

        operations["gtir"] = { a, b, c, registry -> registry[c.toInt()] = if (a > registry[b.toInt()]) 1 else 0 }
        operations["gtri"] = { a, b, c, registry -> registry[c.toInt()] = if (registry[a.toInt()] > b) 1 else 0 }
        operations["gtrr"] = { a, b, c, registry -> registry[c.toInt()] = if (registry[a.toInt()] > registry[b.toInt()]) 1 else 0 }

        operations["eqir"] = { a, b, c, registry -> registry[c.toInt()] = if (a == registry[b.toInt()]) 1 else 0 }
        operations["eqri"] = { a, b, c, registry -> registry[c.toInt()] = if (registry[a.toInt()] == b) 1 else 0 }
        operations["eqrr"] = { a, b, c, registry -> registry[c.toInt()] = if (registry[a.toInt()] == registry[b.toInt()]) 1 else 0 }


        return operations
    }
}
