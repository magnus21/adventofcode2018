package adventofcode

import java.io.File

fun main(args: Array<String>) {

    val operationCodes = OperationCodes()

    val (operationResults, instructions) = operationCodes.parseInput()
    val operations = operationCodes.getOperations()

    val rawInput = File("src/main/resources/19.txt").readLines()

    val (ipRegistry, programInstructions) = Day19.parseInput(rawInput)

    //println(ipRegistry)
    //println(programInstructions)

    // Part one.
    var registry = mutableListOf(0L, 0L, 0L, 0L, 0L, 0L)
    var ipValue = 0L
    Day19.executeInstructions(registry, ipRegistry, ipValue, programInstructions, operations)
    println(registry)

    // Part two.
    registry = mutableListOf(1L, 0L, 0L, 0L, 0L, 0L)
    ipValue = 0L
    // To big loop (fast forward inner loop..)
    Day19.executeInstructions(registry, ipRegistry, ipValue, programInstructions, operations, false)
    println(registry)
    // 27941760

}

data class NamedInstruction(val opName: String, val a: Long, val b: Long, val c: Long)

object Day19 {

    fun executeInstructions(
        registry: MutableList<Long>,
        ipRegistry: Int,
        ipValueStart: Long,
        programInstructions: List<NamedInstruction>,
        operations: MutableMap<String, (Long, Long, Long, MutableList<Long>) -> Unit>,
        debug: Boolean = false
    ) {
        var ipValue = ipValueStart
        do {

            registry[ipRegistry] = ipValue

            val registryBeforeOperation = registry.toMutableList()
            val ins = programInstructions[ipValue.toInt()]
            operations[ins.opName]!!.invoke(ins.a, ins.b, ins.c, registry)

            if (debug) {
                printProgramState(ipValue, registryBeforeOperation, ins, registry)
            }

            ipValue = registry[ipRegistry] + 1

            // Fast forward hack.
            if (ipValue == 4L && registry[3] == 1L && registry[5] == 10551264L) {
                // Fast forward
                if (registry[5] % registry[1] == 0L) {
                    registry[2] = 10551264L
                    registry[3] = 10551264L
                } else {
                    registry[3] = 10551264L
                }

            }
        } while (ipValue < programInstructions.size)
    }

    fun printProgramState(
        ipValue: Long,
        registryBeforeOperation: MutableList<Long>,
        ins: NamedInstruction,
        registry: MutableList<Long>
    ) {
        println("ip=$ipValue $registryBeforeOperation ${ins.opName} ${ins.a} ${ins.b} ${ins.c} $registry")
    }

    fun parseInput(rawInput: List<String>): Pair<Int, List<NamedInstruction>> {
        val instructions = mutableListOf<NamedInstruction>()

        val ipRegistry = Integer.valueOf(rawInput[0].split(" ")[1])

        for (i in 1 until rawInput.size) {
            instructions.add(parseInstruction(rawInput[i]));
        }

        return Pair(ipRegistry, instructions)
    }

    private fun parseInstruction(input: String): NamedInstruction {
        val list = input.split(" ").toList()
        return NamedInstruction(
            list[0],
            Integer.valueOf(list[1]).toLong(),
            Integer.valueOf(list[2]).toLong(),
            Integer.valueOf(list[3]).toLong()
        )
    }
}
/**
addi 4 16 4
seti 1 1 1
while (r3 <= r5) {
    seti 1 7 3                                                                      r3 = 1
    ip=3 [0, 1, 0, 91752, 3, 10551264] mulr 1 3 2 [0, 1, 91752, 91752, 3, 10551264]	r2 = r1 * r3
    ip=4 [0, 1, 91752, 91752, 4, 10551264] eqrr 2 5 2 [0, 1, 0, 91752, 4, 10551264]	r2 = r2 == r5 ? 1 : 0
    ip=5 [0, 1, 0, 91752, 5, 10551264] addr 2 4 4 [0, 1, 0, 91752, 5, 10551264]		*r4 += r2
    ip=6 [0, 1, 0, 91752, 6, 10551264] addi 4 1 4 [0, 1, 0, 91752, 7, 10551264]		*r4++
    (when r2 == 1 -> addr 1 0 0 r0 += r1)
    ip=8 [0, 1, 0, 91752, 8, 10551264] addi 3 1 3 [0, 1, 0, 91753, 8, 10551264]		r3++
    ip=9 [0, 1, 0, 91753, 9, 10551264] gtrr 3 5 2 [0, 1, 0, 91753, 9, 10551264]		r2 = r3 > r5 ? 1 : 0
    ip=10 [0, 1, 0, 91753, 10, 10551264] addr 4 2 4 [0, 1, 0, 91753, 10, 10551264]	*r4 += r2
    ip=11 [0, 1, 0, 91753, 11, 10551264] seti 2 3 4 [0, 1, 0, 91753, 2, 10551264]	*r4 = 2
} when loop ends -> r0 += r1 		-> [1, 1, 1, 10551265, 12, 10551264]
addi 1 1 1 <- r1++
gtrr 1 5 2
addr 2 4 4 GOTO 2 r2 needs to be = 1 -> r1 > r5 -> r1 = 10551265
seti 1 6 4 ********
mulr 4 4 4
addi 5 2 5
mulr 5 5 5
mulr 4 5 5
muli 5 11 5
addi 2 1 2
mulr 2 4 2
addi 2 6 2
addr 5 2 5
addr 4 0 4
seti 0 0 4
setr 4 5 2
mulr 2 4 2
addr 4 2 2
mulr 4 2 2
muli 2 14 2
mulr 2 4 2
addr 5 2 5
seti 0 5 0
seti 0 2 4
 */