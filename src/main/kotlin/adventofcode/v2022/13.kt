package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day13 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "13.txt")

        printResult("part 1") { part1(input) }
        printResult("part 2") { part2(input) }
    }

    private fun part1(rows: List<String>): Int {
        val pairs = rows.chunked(3).map {
            Pair(it[0], it[1])
        }
        return pairs
            .mapIndexed { i, it -> Pair(i + 1, hasCorrectOrder(it.first, it.second)) }
            .filter { it.second >= 0 }
            .sumOf { it.first }
    }

    private fun part2(rows: List<String>): Int {
        val sorted = rows
            .filter { it != "" }
            .plus("[[2]]")
            .plus("[[6]]")
            .sortedWith { s1, s2 -> hasCorrectOrder(s1, s2) }
            .reversed()

        return sorted
            .mapIndexed { i, it -> if (it == "[[2]]" || it == "[[6]]") Pair(true, i + 1) else Pair(false, 0) }
            .filter { it.first }
            .map { it.second }
            .reduce { a, b -> a * b }
    }

    private fun hasCorrectOrder(left: String, right: String): Int {
        return when {
            left.all(Char::isDigit) && right.all(Char::isDigit) -> right.toInt().compareTo(left.toInt())
            else -> {
                val leftList = if (left.startsWith('[')) getItems(left) else listOf(left)
                val rightList = if (right.startsWith('[')) getItems(right) else listOf(right)
                compareLists(leftList, rightList)
            }
        }
    }

    private fun getItems(str: String): List<String> {
        return splitStr(str.drop(1).dropLast(1))
    }

    private fun compareLists(leftList: List<String>, rightList: List<String>): Int {
        leftList.forEachIndexed { i, l ->
            if (rightList.size - 1 < i) {
                return -1
            }
            val res = hasCorrectOrder(l, rightList[i])
            if (res != 0) {
                return res
            }
        }
        return if (leftList.size == rightList.size) 0 else 1
    }

    private fun splitStr(str: String): List<String> {
        return str.foldIndexed(Triple(0, "", listOf<String>())) { i, (level, acc, list), c ->
            when {
                i == str.length - 1 -> Triple(level - 1, "", list.plus(acc + c))
                level == 0 && c == ',' -> Triple(level, "", list.plus(acc))
                c == '[' -> Triple(level + 1, acc + c, list)
                c == ']' -> Triple(level - 1, acc + c, list)
                else -> Triple(level, acc + c, list)
            }
        }.third
    }
}