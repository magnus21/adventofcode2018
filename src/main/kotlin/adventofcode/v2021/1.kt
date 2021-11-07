package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day1 {

    @JvmStatic
    fun main(args: Array<String>) {

        val entries = FileParser.getFileRows(2021, "1.txt").map { Integer.valueOf(it) }

        val time1 = measureTimeMillis {
            println("answer part 1: ")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            println("answer part 2: ")
        }
        println("Time: $time2 ms")
    }
}