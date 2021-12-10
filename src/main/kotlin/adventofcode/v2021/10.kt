package adventofcode.v2021

import adventofcode.util.FileParser
import adventofcode.v2021.Day10.RowType.*
import java.util.*
import kotlin.system.measureTimeMillis

object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {
        val chunks = FileParser.getFileRows(2021, "10.txt")

        val charMap = mutableMapOf(
            Pair('(', ')'),
            Pair('[', ']'),
            Pair('{', '}'),
            Pair('<', '>')
        )

        val parsedRows = chunks.map { chunk -> parseRow(chunk, charMap) }

        val time1 = measureTimeMillis {
            val scoreMap = mutableMapOf(
                Pair(')', 3),
                Pair(']', 57),
                Pair('}', 1197),
                Pair('>', 25137)
            )

            val answer = parsedRows
                .filter { it.rowType == CORRUPT }
                .sumOf { scoreMap[it.found]!! }

            println("answer part 1: $answer")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val scoreMap = mutableMapOf(
                Pair(')', 1),
                Pair(']', 2),
                Pair('}', 3),
                Pair('>', 4)
            )

            val scores = parsedRows
                .filter { it.rowType == INCOMPLETE }
                .map { it.remainingStack.reversed().fold(0L) { acc, c -> acc * 5 + scoreMap[charMap[c]!!]!! } }
                .sorted()

            println("answer part 2: ${scores[scores.size / 2]}")
        }
        println("Time: $time2 ms")
    }

    private fun parseRow(chunk: String, charMap: MutableMap<Char, Char>): ParseResult {
        val pushChars = charMap.keys
        val stack = Stack<Char>()
        for (c in chunk) {
            if (pushChars.contains(c)) {
                stack.push(c)
            } else {
                val lastPushed = stack.pop()
                if (charMap[lastPushed] != c) {
                    return ParseResult(CORRUPT, charMap[lastPushed]!!, c)
                }
            }
        }
        return if (stack.empty()) ParseResult(OK)
        else ParseResult(INCOMPLETE, remainingStack = stack)
    }

    data class ParseResult(
        val rowType: RowType,
        val expected: Char = ' ',
        val found: Char = ' ',
        val remainingStack: Stack<Char> = Stack()
    )

    enum class RowType {
        OK,
        CORRUPT,
        INCOMPLETE
    }
}