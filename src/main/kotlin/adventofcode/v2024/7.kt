package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day7 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val equations = parseInput(FileParser.getFileRows(2024, "7.txt"))

        printResult("part 1") { part1(equations) }
        printResult("part 2") { part2(equations) }
    }

    private val operators = listOf(
        { a: Long, b: Long -> a * b } to "*",
        { a: Long, b: Long -> a + b } to "+",
        { a: Long, b: Long -> (a.toString() + b.toString()).toLong() } to "||"
    )

    private fun part1(equations: List<Equation>): Long {
        return equations.filter { validateEquation(it.numbers[0], it) }.sumOf { it.testValue }
    }

    private fun part2(equations: List<Equation>): Long {
        return equations.filter { validateEquation(it.numbers[0], it) }.sumOf { it.testValue }
    }

    private fun validateEquation(acc: Long, equation: Equation, i: Int = 1, history: String = "$acc"): Boolean {
        return operators.any { op ->
            if (acc == equation.testValue && i == equation.numbers.size) {
                true
            } else if (acc > equation.testValue) false
            else if (i >= equation.numbers.size) false
            else {
                validateEquation(
                    op.first(acc, equation.numbers[i]),
                    equation,
                    i + 1,
                    history + op.second + equation.numbers[i]
                )
            }
        }
    }

    data class Equation(val testValue: Long, val numbers: List<Long>)

    private fun parseInput(input: List<String>): List<Equation> {
        return input.map { row ->
            val mainParts = row.split(":")
            val numbers = mainParts[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() }
            Equation(mainParts[0].toLong(), numbers)
        }
    }
}