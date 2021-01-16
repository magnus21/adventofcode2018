package adventofcode.v2015

import kotlin.system.measureTimeMillis

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = "hepxcrrq"

        val time1 = measureTimeMillis {

            var password = input
            do {
                password = getNextPassword(password)
            } while (!isValid(password))

            println("Part 1: $password")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            var password = "hepxxyzz" // Answer part 1.
            do {
                password = getNextPassword(password)
            } while (!isValid(password))

            println("Part 2: $password")
        }
        println("Time: $time2 ms")
    }

    private fun isValid(password: String): Boolean {

        if (listOf('i', 'o', 'l').any { password.contains(it) }) {
            return false
        }

        return getPairCount(password) > 1 && hasStraight(password)
    }

    private fun hasStraight(password: String): Boolean {
        var i = 0
        while (i < password.length) {
            val c = password[i]
            if (i + 2 < password.length && c == password[i + 1] - 1 && c == password[i + 2] - 2) {
                return true
            }
            i++
        }
        return false
    }

    private fun getPairCount(password: String): Int {
        var i = 0
        var pairCount = 0
        while (i < password.length) {
            val c = password[i]
            if (i + 1 < password.length && c == password[i + 1]) {
                pairCount++
                i += 2
            } else {
                i++
            }
        }
        return pairCount
    }

    private fun getNextPassword(password: String): String {

        val reversedPassword = password.reversed()
        var i = 0
        while (reversedPassword[i] == 'z' && i < reversedPassword.length) {
            i++
        }

        return when {
            i == 0 -> password.dropLast(i + 1).plus(reversedPassword[i] + 1)
            else -> password.dropLast(i + 1).plus(reversedPassword[i] + 1).plus("".padEnd(i, 'a'))
        }
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