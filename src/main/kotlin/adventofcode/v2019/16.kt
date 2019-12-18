package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.LinkedList
import adventofcode.util.Node
import kotlin.system.measureTimeMillis


object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = //FileParser.getFileRows(2019, "16.txt")
           listOf("03036732577212944063491565474664")
            .flatMap { it.toCharArray().toList() }
            .map { it.toString().toInt() }

        val inputAsLinkedList =  LinkedList.fromList(input)

        val basePattern = listOf(0, 1, 0, -1)

        val time1 = measureTimeMillis {

            val arr1 = Array(10) { i -> i}
            val arr2 = Array(10) { i -> i}

            doFFT(inputAsLinkedList, basePattern, input.size)
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val input10000AsLinkedList =  LinkedList.fromList(input)
            for(i in 1 until 10000) {
                input.forEach { input10000AsLinkedList.append(it) }
            }

            doFFT(input10000AsLinkedList, basePattern,input.size*10000)
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun doFFT(
        inputList: LinkedList<Int>,
        basePattern: List<Int>,
        listSize: Int
    ) {
        var input = inputList
        for (c in 1..100) {
            var outputList: LinkedList<Int>? = null
            for (pos in 0 until listSize) {
                val patternForPosition = basePattern.flatMap { nr -> (0..pos).map { nr } }
                val pattern =
                    (1..(listSize / patternForPosition.size) + 1).flatMap { patternForPosition }.drop(1)

                val patternAsLinkedList =  LinkedList.fromList(pattern)

                input.startFromHead()
                patternAsLinkedList.startFromHead()
                var sum = 0
                for (i in 0 until listSize) {
                    sum += input.getCurrent().value * patternAsLinkedList.getCurrent().value

                    if(i != (listSize - 1)) {
                        input.stepToNext()
                        patternAsLinkedList.stepToNext()
                    }
                }
                println("Row/pos $pos")
                val value = Math.abs(sum) % 10
                if(outputList == null) {
                    outputList = LinkedList.fromList(listOf(value))
                } else {
                    outputList.append(Math.abs(sum) % 10)
                }
            }
            outputList!!.startFromHead()
            input = outputList

            print("After $c phases: ")
            outputList.startFromHead()
            outputList.toList(8).take(8).forEach(::print)
            println()
        }
    }
}