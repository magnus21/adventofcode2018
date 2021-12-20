package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2021, "18.txt")

        val time1 = measureTimeMillis {
            val reducedSnailNumber = reduceSnailNumbers(input)
            println("Reduced snail number: $reducedSnailNumber")

            val answer = calculateMagnitude(reducedSnailNumber)
            println("answer part 1: $answer")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            var maxMagnitude = -1L
            for (i in input.indices) {
                for (j in input.indices) {
                    if (i != j) {
                        val reducedSnailNumber = reduceSnailNumbers(listOf(input[i], input[j]))
                        val magnitude = calculateMagnitude(reducedSnailNumber)
                        maxMagnitude = if(magnitude > maxMagnitude) magnitude else maxMagnitude
                    }
                }
            }
            println("answer part 2: $maxMagnitude")
        }
        println("Time: $time2 ms")
    }

    private fun reduceSnailNumbers(input: List<String>) =
        input.drop(1).fold(input[0]) { acc, row ->
            var str = "[$acc,$row]"
            do {
                var firstNumberToSplit: Pair<Int, Int>? = null
                val firstLevelFourPair = findFirstLevel4Pair(str)

                if (firstLevelFourPair != null) {
                    str = explodeLevelFourPair(str, firstLevelFourPair)
                } else {
                    firstNumberToSplit = findFirstNumberToSplit(str)
                    if (firstNumberToSplit != null) {
                        str = splitNumber(str, firstNumberToSplit)
                    }
                }
            } while (firstLevelFourPair != null || firstNumberToSplit != null)

            str
        }

    private fun findFirstNumberToSplit(str: String): Pair<Int, Int>? {
        var searchStartIndex = 0
        while (searchStartIndex < str.length) {
            val leftToSearch = str.substring(searchStartIndex)
            val firstDigit = leftToSearch.indexOfFirst { it.isDigit() }
            if (firstDigit != -1) {
                val number = leftToSearch.substring(firstDigit).takeWhile { it.isDigit() }.toInt()
                if (number > 9) {
                    return Pair(searchStartIndex + firstDigit, number)
                }
            } else {
                return null
            }
            searchStartIndex += firstDigit + 1
        }
        return null
    }

    private fun explodeLevelFourPair(
        str: String,
        firstLevelFourPair: Pair<Int, String>
    ): String {
        var leftStr = str.substring(0, firstLevelFourPair.first - 1)
        var rightStr = str.substring(firstLevelFourPair.first + firstLevelFourPair.second.length + 1)

        val leftNumber = findFirstNumber(leftStr, true)
        val rightNumber = findFirstNumber(rightStr)

        val pairNumberStrings = firstLevelFourPair.second.split(",")
        if (leftNumber != null) {
            leftStr = explodeUpdate(leftStr, leftNumber, Pair(firstLevelFourPair.first, pairNumberStrings[0]))
        }
        if (rightNumber != null) {
            rightStr = explodeUpdate(
                rightStr,
                rightNumber,
                Pair(firstLevelFourPair.first + pairNumberStrings[0].length + 1, pairNumberStrings[1])
            )
        }
        return leftStr + '0' + rightStr
    }

    private fun explodeUpdate(
        str: String,
        number: Pair<Int, String>,
        levelFourPairPart: Pair<Int, String>
    ): String {
        return str.substring(0, number.first) +
                (number.second.toInt() + levelFourPairPart.second.toInt()) +
                str.substring(number.first + number.second.length)
    }

    private fun splitNumber(str: String, number: Pair<Int, Int>): String {
        val newPairStr = "[${number.second / 2},${ceil(number.second.toDouble() / 2).toInt()}]"
        return str.substring(0, number.first) +
                newPairStr +
                str.substring(number.first + number.second.toString().length)
    }

    private fun findFirstNumber(str: String, startFromEnd: Boolean = false): Pair<Int, String>? {
        if (startFromEnd) {
            val numberIndex = str.indexOfLast { it.isDigit() }
            if (numberIndex > -1) {
                val number = str.substring(0, numberIndex + 1).takeLastWhile { it.isDigit() }
                return Pair(numberIndex - (number.length - 1), number)
            }
        } else {
            val numberIndex = str.indexOfFirst { it.isDigit() }
            if (numberIndex > -1) {
                val number = str.substring(numberIndex).takeWhile { it.isDigit() }
                return Pair(numberIndex, number)
            }
        }
        return null
    }

    private val numberPairRegex = """(\d+),(\d+)""".toRegex()
    private fun findFirstLevel4Pair(str: String): Pair<Int, String>? {
        var level = 0
        for (i in str.indices) {
            if (level == 5) {
                val matchResult = numberPairRegex.find(str.substring(i))
                if (matchResult != null && matchResult.range.first == 0) {
                    return Pair(i, matchResult.value)
                }
            }
            level = when (str[i]) {
                '[' -> level + 1
                ']' -> level - 1
                else -> level
            }
        }
        return null
    }

    private fun calculateMagnitude(str: String): Long {
        if (str.all { it.isDigit() }) {
            return str.toLong()
        }
        val (first, second) = parsePair(str)
        return 3 * calculateMagnitude(first) + 2 * calculateMagnitude(second)
    }

    private fun parsePair(str: String): Pair<String, String> {
        val pairStr = str.drop(1).dropLast(1)
        val commaIndex = findMiddleCommaIndex(pairStr)
        return Pair(pairStr.substring(0, commaIndex), pairStr.substring(commaIndex + 1))
    }

    private fun findMiddleCommaIndex(pairStr: String): Int {
        var level = 0
        for (i in pairStr.indices) {
            level = when (pairStr[i]) {
                '[' -> level + 1
                ']' -> level - 1
                else -> level
            }
            if (level == 0) {
                return i + 1
            }
        }
        return -1
    }
}