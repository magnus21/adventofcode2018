package adventofcode

import java.io.File

fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/16.txt").readLines()
    val (operationResults, instructions) = parseInput(rawInput)

    //println(operationResults)
    //println(registries)

    val operations = getOperations()

    val partOneAnswer = operationResults.map {  operationResult ->
        val ins = operationResult.instruction
        operations.filter { operation ->
            val registry = operationResult.beforeState.toMutableList()
            operation.value(ins.a, ins.b, ins.c, registry)
            registry == operationResult.afterState
            }.size
    }.filter { it >= 3 }.size

    println(partOneAnswer)

    val opCodes = operationResults.map { it.instruction.opCode }.distinct().sorted()

    var operationsMatched = opCodes.map { opCode ->
        val matchingOperationsResults = operationResults
            .filter { it.instruction.opCode == opCode }

        val matchingOperations = operations.filter { operation ->
            matchingOperationsResults.all {operationResult ->
                val ins = operationResult.instruction
                val registry = operationResult.beforeState.toMutableList()
                operation.value(ins.a, ins.b, ins.c, registry)
                registry == operationResult.afterState
            }
        }

        Pair(opCode, matchingOperations.map { it.key })
    }

    // Reduce
    while(operationsMatched.any{ it.second.size > 1 }){
        val oneMatches = operationsMatched
            .filter { it.second.size == 1 }.toMutableList()

        val oneOps = operationsMatched
            .filter { it.second.size == 1 }
            .flatMap { it.second }

        val reduced =operationsMatched
            .filter { it.second.size > 1 }
            .map { Pair(it.first,it.second.filter { op -> !oneOps.contains(op) })}

        oneMatches.addAll(reduced)
        operationsMatched = oneMatches
    }

    val opCodeMap = operationsMatched.map { Pair(it.first,it.second[0]) }

    println(opCodeMap)

    val registry = mutableListOf(0,0,0,0)
    for(ins in instructions) {
        val operation = operations[opCodeMap.filter { it.first == ins.opCode}.map { it.second }.first()]
        operation?.invoke(ins.a, ins.b, ins.c, registry)
    }

    println(registry)
}

data class OperationResult(var beforeState: List<Int>, var afterState: List<Int>, var instruction: Instruction)
data class Instruction(val opCode: Int, val a: Int, val b: Int, val c: Int)


fun parseInput(rawInput: List<String>): Pair<List<OperationResult>, List<Instruction>> {
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
    val list = input.split(" ").map { Integer.valueOf(it) }.toList()
    return Instruction(list[0], list[1], list[2], list[3])
}

fun parseRegistry(input: String): List<Int> {
    return input.substring(9, input.length - 1).split(", ").map { Integer.valueOf(it) }.toList()
}

fun getOperations(): MutableMap<String, (Int, Int, Int, MutableList<Int>) -> Unit> {
    val operations = mutableMapOf<String, (Int, Int, Int, MutableList<Int>) -> Unit>()

    operations["addr"] = { a, b, c, registry -> registry[c] = registry[a] + registry[b] }
    operations["addi"] = { a, b, c, registry -> registry[c] = registry[a] + b }

    operations["mulr"] = { a, b, c, registry -> registry[c] = registry[a] * registry[b] }
    operations["muli"] = { a, b, c, registry -> registry[c] = registry[a] * b }

    operations["banr"] = { a, b, c, registry -> registry[c] = registry[a].and(registry[b]) }
    operations["bani"] = { a, b, c, registry -> registry[c] = registry[a].and(b) }

    operations["borr"] = { a, b, c, registry -> registry[c] = registry[a].or(registry[b]) }
    operations["bori"] = { a, b, c, registry -> registry[c] = registry[a].or(b) }

    operations["setr"] = { a, b, c, registry -> registry[c] = registry[a] }
    operations["seti"] = { a, b, c, registry -> registry[c] = a }

    operations["gtir"] = { a, b, c, registry -> registry[c] = if (a > registry[b]) 1 else 0 }
    operations["gtri"] = { a, b, c, registry -> registry[c] = if (registry[a] > b) 1 else 0 }
    operations["gtrr"] = { a, b, c, registry -> registry[c] = if (registry[a] > registry[b]) 1 else 0 }

    operations["eqir"] = { a, b, c, registry -> registry[c] = if (a == registry[b]) 1 else 0 }
    operations["eqri"] = { a, b, c, registry -> registry[c] = if (registry[a] == b) 1 else 0 }
    operations["eqrr"] = { a, b, c, registry -> registry[c] = if (registry[a] == registry[b]) 1 else 0 }


    return operations
}
