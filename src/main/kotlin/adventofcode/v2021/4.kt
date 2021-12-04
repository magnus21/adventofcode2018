package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {

        val (numbers, boards) = parseInput(FileParser.getFileRows(2021, "4.txt"))

        val time = measureTimeMillis {
            for (nr in numbers) {
                boards.filter { b -> !b.hasBingo }
                    .forEach { b ->
                        b.numbers.flatten().filter { it.number == nr }.forEach { it.marked = true }
                        if (hasBingo(b)) {
                            val bingoCount = boards.count(BingoBoard::hasBingo)
                            if (bingoCount == 1 || bingoCount == boards.size) {
                                val unmarkedSum = b.numbers.flatten().filter { !it.marked }.sumOf { it.number }
                                println("Bingo: ${nr * unmarkedSum}")
                            }
                        }
                    }
                if (boards.all(BingoBoard::hasBingo)) {
                    break
                }
            }
        }
        println("Time: $time ms")
    }

    private fun hasBingo(board: BingoBoard): Boolean {
        val horizontalBingo = board.numbers.filter { it.all(BingoNumber::marked) }.any()
        val verticalBingo = (0 until board.numbers[0].size).any { pos -> board.numbers.all { row -> row[pos].marked } }
        board.hasBingo = horizontalBingo || verticalBingo
        return board.hasBingo
    }

    private fun parseInput(fileRows: List<String>): Pair<List<Int>, List<BingoBoard>> {
        val numbers = fileRows[0].split(",").map { Integer.valueOf(it) }
        val boards = fileRows.drop(1)
            .fold(mutableListOf<BingoBoard>()) { boards, row ->
                when {
                    row.trim().isEmpty() -> boards.add(BingoBoard())
                    else -> boards.last().numbers.add(
                        row.trim().split(" ")
                            .filter { it.isNotEmpty() }
                            .map { BingoNumber(Integer.valueOf(it)) }
                    )
                }
                boards
            }

        return Pair(numbers, boards)
    }

    data class BingoNumber(val number: Int, var marked: Boolean = false)
    data class BingoBoard(
        val numbers: MutableList<List<BingoNumber>> = mutableListOf(),
        var hasBingo: Boolean = false
    )
}