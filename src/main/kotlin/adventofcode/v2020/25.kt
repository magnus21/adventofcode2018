package adventofcode.v2020

import kotlin.system.measureTimeMillis

object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {

        val cardPublicKey = 13135480L
        val doorPublicKey = 8821721L
        val subjectNumber = 7L

        val time = measureTimeMillis {
            val doorLoopSize = findLoopSize(doorPublicKey, subjectNumber).toLong()

            println("Part 1: ${calculateEncryptionKey(cardPublicKey, doorLoopSize)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun findLoopSize(key: Long, subjectNumber: Long): Int {
        var count = 1
        var value = 1L
        while (true) {
            value = (value * subjectNumber) % 20201227

            if (key == value) {
                return count
            }
            count++
        }
    }

    private fun calculateEncryptionKey(subjectNumber: Long, loopSize: Long): Long {
        var value = 1L
        (1..loopSize).forEach { _ -> value = (value * subjectNumber) % 20201227 }
        return value
    }

}