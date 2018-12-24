package adventofcode

import java.io.File

fun main(args: Array<String>) {

    val operationCodes = OperationCodes()
    operationCodes.parseInput()

    val operations = operationCodes.getOperations()

    val rawInput = File("src/main/resources/21.txt").readLines()

    val (ipRegistry, programInstructions) = Day21.parseInput(rawInput)

    // Part one. // r3 is 10147168 first time at ins #28 which has the end condition (r0 == r3).
    var registry = mutableListOf(0L, 0L, 0L, 0L, 0L, 0L)
    var ipValue = 0L
    Day21.executeInstructions(registry, ipRegistry, ipValue, programInstructions, operations, true)
    println(registry)
}

object Day21 {

    fun executeInstructions(
        registry: MutableList<Long>,
        ipRegistry: Int,
        ipValueStart: Long,
        programInstructions: List<NamedInstruction>,
        operations: MutableMap<String, (Long, Long, Long, MutableList<Long>) -> Unit>,
        debug: Boolean = false
    ) {
        var ipValue = ipValueStart
        val r3Set = mutableSetOf<Long>()
        var counter = 0
        do {
            registry[ipRegistry] = ipValue

            val registryBeforeOperation = registry.toMutableList()
            val ins = programInstructions[ipValue.toInt()]
            operations[ins.opName]!!.invoke(ins.a, ins.b, ins.c, registry)

            if (debug) {
                //printProgramState(ipValue, registryBeforeOperation, ins, registry)
            }

            // too low: 1177631, 11212288 7018099
            if (ipValue == 28L) {
                printProgramState(ipValue, registryBeforeOperation, ins, registry)
                if (!r3Set.contains(registry[3])) {
                    r3Set.add(registry[3])
                } else {
                    println("Repeated r3 value: counter: $counter: value: " + registry[3])
                    break
                }

                counter++
            }

            ipValue = registry[ipRegistry] + 1

            if (ipValue == 20L) {
                // Fast forward slow loop.
                registry[5] = (registry[2] / 256 + 1) * 256
                registry[1] = registry[5] / 256 - 1
                //printProgramState(ipValue, registryBeforeOperation, ins, registry)
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
/*
seti 123 0 3		r3 = 123
bani 3 456 3		r3 &= 456
eqri 3 72 3			r3 = r3 == 72 ? 1 : 0.  // Elf check
addr 3 4 4		-	r4 += r3
seti 0 0 4		-	r4 = 0    // if test fails we get here, loop back to ins #1 -> infinite loop.
seti 0 4 3			r3 = 0    // if test ok we get here
bori 3 65536 2		r2 = r3 | 65536			: ins #6
seti 1099159 8 3	r3 = 1099159           	: ins #7
bani 2 255 1		r1 = r2 & 255  			: ins #8
addr 3 1 3			r3 += r1
bani 3 16777215 3	r3 &= 16777215
muli 3 65899 3		r3 *= 65899
bani 3 16777215 3	r3 &= 16777215
gtir 256 2 1		r1 = 256 > r2 ? 1 : 0   <------------------ r2 < 256
addr 1 4 4		-	r4 += r1
addi 4 1 4		-	r4++
seti 27 6 4		-	r4 = 27 // r2 < 256 -> ins #28 <----------------------
seti 0 8 1			r1 = 0	// r2 >= 256
addi 1 1 5			r5 = r1 + 1				 : ins #18 loop start {
muli 5 256 5		r5 *= 256
gtrr 5 2 5			r5 = r5 > r2 ? 1 : 0
addr 5 4 4		-	r4 += r5
addi 4 1 4		-	r4++
seti 25 5 4		-	r4 = 25 goto ins #26
addi 1 1 1			r1++
seti 17 1 4		-	r4 = 17 // loop ends      }
setr 1 2 2			r2 = r1 				:ins #26
seti 7 0 4		-	r4 = 7  // goto ins #8
eqrr 3 0 1			r1 = r3 == r0 ? 1 : 0  // : ins #28 r0 test. r3 == r0 -> program ends
addr 1 4 4		-	r4 += r1
seti 5 0 4		-	r4 = 5 --> to :ins #6

========= Day 21 =========

#ip 4

r3 = 0
r2 = r3 & 65536

0 & 65536 < 256

r3 = 1099159
r3 += r1
r3 &= 16777215
r3 *= 65899
r3 &= 16777215

r2 < 256


(r5/256) -1 = r1
 */