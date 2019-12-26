package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "23.txt").map(String::toLong)

        // Run program.
        val time = measureTimeMillis {

            val nat = Nat()
            val computers = (0..49L)
                .map { Pair(it, NetworkNode(it, IntCodeComputer(input.toMutableList()), Queue())) }
                .toMap()

            //Boot up!
            computers.values.forEach { it.computer.runWithInput(listOf(it.address)) }

            // Run
            var idleCount = 0
            val natYs = mutableSetOf<Long>()
            while (true) {
                idleCount = 0
                computers.values.forEach {
                    val nextInput = if (it.inputQueue.isEmpty()) -1L else it.inputQueue.dequeue()!!
                    val result = it.computer.runWithInput(listOf(nextInput))

                    if (result.second == IntCodeComputer.WAITING_FOR_INPUT) {
                        idleCount++
                    }
                    sendOutput(result, computers, nat)
                }

                if (idleCount == computers.size && computers.values.none { it.inputQueue.isNotEmpty() }) {
                    computers[0]!!.inputQueue.enqueue(nat.x)
                    computers[0]!!.inputQueue.enqueue(nat.y)
                    if (natYs.contains(nat.y)) {
                        println("Answer part 2: ${nat.y}")
                        break
                    }
                    natYs.add(nat.y)
                }
            }
        }
        println("Time part: ($time milliseconds)")
    }

    private fun sendOutput(
        result: Pair<MutableList<Long>, Int>,
        computers: Map<Long, NetworkNode>,
        nat: Nat
    ) {
        val output = result.first
        if (output.isNotEmpty()) {
            for (i in 0 until output.size step 3) {
                val address = output[i]
                if (address < computers.size) {
                    computers[address]!!.inputQueue.enqueue(output[i + 1])
                    computers[address]!!.inputQueue.enqueue(output[i + 2])
                } else if (address == 255L) {
                    //println("Answer part 1: ${output[i + 2]}")
                    println("NAT input(address,x,y): $output")
                    nat.x = output[i + 1]
                    nat.y = output[i + 2]
                }
            }
        }
    }

    data class NetworkNode(val address: Long, val computer: IntCodeComputer, val inputQueue: Queue<Long>)

    data class Nat(var x: Long = -1, var y: Long = -1)

}