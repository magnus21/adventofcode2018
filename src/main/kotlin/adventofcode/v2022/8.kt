package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day8 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "8.txt")
            .map { row -> row.map { it.toString().toInt() } }

        val ySize = input.size
        val xSize = input[0].size

        printResult("part 1") {
            part1(ySize, xSize, input)
        }
        printResult("part 2") {
            part2(ySize, xSize, input)
        }
    }

    private fun part1(ySize: Int, xSize: Int, input: List<List<Int>>): Int {
        var count = 0
        (1 until ySize - 1).forEach { y ->
            (1 until xSize - 1).forEach { x ->
                val treeHeight = input[y][x]

                if (
                    input[y].subList(0, x).all { it < treeHeight } ||
                    input[y].subList(x + 1, xSize).all { it < treeHeight } ||
                    input.map { it[x] }.subList(0, y).all { it < treeHeight } ||
                    input.map { it[x] }.subList(y + 1, ySize).all { it < treeHeight }
                ) {
                    count++
                }
            }
        }
        return count + ySize * 2 + (xSize - 2) * 2
    }

    fun part2(ySize: Int, xSize: Int, input: List<List<Int>>): Long? {
        return (0 until ySize).flatMap { y ->
            (0 until xSize).map { x ->
                val treeHeight = input[y][x]

                val scoreXL = if (x == 0) 0
                else getScore(input[y].subList(0, x).reversed(), treeHeight)

                val scoreXR = if (x == xSize - 1) 0
                else getScore(input[y].subList(x + 1, xSize), treeHeight)

                val scoreYT = if (y == 0) 0
                else getScore(input.map { it[x] }.subList(0, y).reversed(), treeHeight)

                val scoreYB = if (y == ySize - 1) 0
                else getScore(input.map { it[x] }.subList(y + 1, ySize), treeHeight)

                scoreXL * scoreXR * scoreYT * scoreYB
            }
        }.maxOrNull()
    }

    private fun getScore(input: List<Int>, treeHeight: Int): Long {
        val count = input.takeWhile { it < treeHeight }.count()
        return count + if (count < input.size) 1L else 0
    }
}