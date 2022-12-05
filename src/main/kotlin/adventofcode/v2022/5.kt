package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day5 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "5.txt")

        printResult("part 1") {
            val (stacks, moves) = parseInput(input)
            moves.forEach { move -> doMove(stacks, move, List<String>::reversed) }
            stacks.joinToString("") { it.last() }
        }

        printResult("part 2") {
            val (stacks, moves) = parseInput(input)
            moves.forEach { move -> doMove(stacks, move) { l -> l } }
            stacks.joinToString("") { it.last() }
        }
    }

    private fun doMove(
        stacks: MutableList<MutableList<String>>,
        move: Move,
        transform: (l: List<String>) -> List<String>
    ) {
        val moved = stacks[move.from].takeLast(move.count)
        stacks[move.to].addAll(transform.invoke(moved))
        stacks[move.from] = stacks[move.from].dropLast(move.count).toMutableList()
    }

    private fun parseInput(rows: List<String>): Pair<MutableList<MutableList<String>>, List<Move>> {
        val stacksRows = rows.takeWhile { it.isNotEmpty() }
            .map { row ->
                row.chunked(4).map {
                    it.replace("[", "").replace("]", "").trim()
                }
            }.dropLast(1)

        val nrOfStacks = stacksRows.maxOf { it.size }
        val stacks = (0 until nrOfStacks).map { i ->
            (stacksRows.size - 1 downTo 0).mapNotNull {
                stacksRows[it].elementAtOrNull(i)
            }.filter { it.isNotEmpty() }.toMutableList()
        }.toMutableList()

        val moves = rows.takeLast(rows.size - stacksRows.size - 2)
            .map {
                "move (\\d+) from (\\d+) to (\\d+)".toRegex().matchEntire(it)?.destructured
                    ?.let { (m1, m2, m3) -> Move(m1.toInt(), m2.toInt() - 1, m3.toInt() - 1) }
                    ?: throw IllegalArgumentException("Bad input '$it'")
            }

        return Pair(stacks, moves)
    }

    private data class Move(val count: Int, val from: Int, val to: Int)
}