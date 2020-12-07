package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {

        val rows = FileParser.getFileRows(2020, "2.txt").map { Day2.getPasswordEntry(it) }


        val time1 = measureTimeMillis {
            println("answer part 1: " + getValidPasswordCountPart1(rows))
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            println("answer part 2: " + getValidPasswordCountPart2(rows))
        }
        println("Time: $time2 ms")
    }


    data class PasswordEntry(
        val policyLetter: Char,
        val policyLetterIndex1: Int,
        val policyLetterIndex2: Int,
        val password: String
    ) {
        fun confirmsToLetterPolicyPart1(): Boolean {
            return password.filter { it == policyLetter }.length in policyLetterIndex1..policyLetterIndex2
        }

        fun confirmsToLetterPolicyPart2(): Boolean {
            return (password[policyLetterIndex1 - 1] == policyLetter)
                .xor(password[policyLetterIndex2 - 1] == policyLetter)
        }
    }

    fun getValidPasswordCountPart1(rows: List<PasswordEntry>): Int {
        return rows.filter { it.confirmsToLetterPolicyPart1() }.size
    }

    fun getValidPasswordCountPart2(rows: List<PasswordEntry>): Int {
        return rows.filter { it.confirmsToLetterPolicyPart2() }.size
    }

    fun getPasswordEntry(row: String): PasswordEntry {
        val parts = row.split("-", " ")
        return PasswordEntry(parts[2][0], Integer.valueOf(parts[0]), Integer.valueOf(parts[1]), parts[3])
    }
}