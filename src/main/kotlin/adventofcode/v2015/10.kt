package adventofcode.v2015

import kotlin.system.measureTimeMillis

object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {

        val digits = "1321131112"

        val time1 = measureTimeMillis {
            val result = (1..40).fold(digits) { acc, _ -> generateNextSequence(acc) }.length
            println("Part 1: $result")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val result = (1..50).fold(digits) { acc, _ -> generateNextSequence(acc) }.length
            println("Part 2: $result")
        }
        println("Time: $time2 ms")
    }

    private fun generateNextSequence(sequence: String): String {

        // The string builder is the key -> no new object after each string concatenation (that's is too expensive)!!
        val result = StringBuilder()

        var i = 0
        val len = sequence.length
        while (i < len) {
            var count = 1
            val d = sequence[i]
            while (i + 1 < len && sequence[i + 1] == d) {
                i++
                count++
            }
            result.append(count).append(d)
            i++
        }

        return result.toString()
    }
}