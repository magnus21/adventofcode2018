package adventofcode.v2019

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis


object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "16.txt")
            //listOf("69317163492948606335995924319873")
            .flatMap { it.toCharArray().toList() }
            .map { it.toString().toInt() }

        val basePattern = listOf(0, 1, 0, -1)

        val time1 = measureTimeMillis {

            val arr1 = Array(10) { i -> i }
            val arr2 = Array(10) { i -> i }

            val result = doFFT1(input, basePattern).take(8)
            println("Answer part 1: $result")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val input10000 = input.toMutableList()
            for (i in 1 until 10000) {
                input.forEach { input10000.add(it) }
            }
            // Offset
            val offset = input10000.take(7).joinToString("").toInt()

            //doFFT1(input10000, basePattern)
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun doFFT1(
        inputList: List<Int>,
        basePattern: List<Int>
    ): MutableList<Int> {
        var input = inputList.toMutableList()
        var output = mutableListOf<Int>()
        for (c in 1..100) {
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
            if (c != 100) {
                output.clear()
            }
        }
        return output
    }
}