package adventofcode.v2019

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis


object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "16.txt")
            //listOf("03081770884921959731165446850517")
            .flatMap { it.toCharArray().toList() }
            .map { it.toString().toInt() }

        val basePattern = listOf(0, 1, 0, -1)

        val time1 = measureTimeMillis {
            val result = doFFT1(input, basePattern).take(8)
            println("Answer part 1: ${result.joinToString("")}")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val input10000 = input.toMutableList()
            for (i in 1 until 10000) {
                input.forEach { input10000.add(it) }
            }
            // Offset
            val offset = input10000.take(7).joinToString("").toInt()

            println("Offset: $offset, size: ${input10000.size}")

            // Skip until offset since offset > input10000.size/2
            val inputFromOffset = input10000.subList(offset,input10000.size).toMutableList()
            for (phase in 1..100) {
                for (i in (inputFromOffset.size - 1) downTo 0) {
                    inputFromOffset[i] = Math.abs((if(i + 1 > (inputFromOffset.size - 1)) 0 else inputFromOffset[i + 1]) + inputFromOffset[i]) % 10
                }
            }
            // 59775675 too high
            println("Answer part 2: ${inputFromOffset.take(8).joinToString("")}")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun doFFT1(
        inputList: List<Int>,
        basePattern: List<Int>
    ): MutableList<Int> {
        var input = inputList.toMutableList()
        val output = mutableListOf<Int>()
        for (phase in 1..100) {
            for (pos in 0 until inputList.size) {
                var sum = 0
                for (i in pos until inputList.size) {
                    val patternDigit = basePattern[((i + 1) / (pos + 1)) % 4];
                    sum += input[i] * patternDigit

                    //print("${input[i]}*${patternDigit} + ")
                }
                //println(" = ${Math.abs(sum % 10)}")
                output.add(Math.abs(sum % 10))
            }
            input = output.toMutableList()
            if (phase != 100) {
                output.clear()
            }
        }
        return output
    }
}