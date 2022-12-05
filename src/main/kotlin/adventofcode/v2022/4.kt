package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.min
import kotlin.time.ExperimentalTime

object Day4 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "4.txt").map(::parseInputRow)

        printResult("part 1") {
            input.count { (elf1, elf2) -> elf1.intersect(elf2).size == min(elf1.count(), elf2.count()) }
        }

        printResult("part 2") {
            input.count { (elf1, elf2) -> elf1.intersect(elf2).isNotEmpty() }
        }
    }

    private fun parseInputRow(row: String) = "(\\d+)-(\\d+),(\\d+)-(\\d+)".toRegex().matchEntire(row)?.destructured
        ?.let { (s1, s2, s3, s4) -> (s1.toInt()..s2.toInt()) to (s3.toInt()..s4.toInt()) }
        ?: throw IllegalArgumentException("Bad input '$row'")
}