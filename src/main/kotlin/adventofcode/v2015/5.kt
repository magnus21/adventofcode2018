package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2015, "5.txt")

        val time1 = measureTimeMillis {

            val vowels = "aeiou".toSet()
            val notAllowed = listOf("ab", "cd", "pq", "xy")

            val niceRowsCount = input.filter { row ->
                val vowelCountIsAtLeastThree = row.toList().count { vowels.contains(it) } >= 3
                val foundDouble = row.fold(Pair(' ', 0)) { acc, c ->
                    if (c == acc.first) Pair(c, acc.second + 1) else Pair(c, acc.second)
                }.second > 0

                val doesNotContainNonAllowedString = notAllowed.count { row.contains(it) } == 0

                vowelCountIsAtLeastThree && foundDouble && doesNotContainNonAllowedString
            }.count()

            println("Part 1: $niceRowsCount")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val niceRowsCount = input.filter { row ->
                val foundDoublePair = findDoublePair(row)

                val repeatingLetterWithOneInBetween = row.fold(Pair(listOf(' ', ' '), 0)) { acc, c ->
                    val count = if (acc.first.dropLast(1).last() == c) 1 else 0
                    Pair(acc.first.plus(c), acc.second + count)
                }.second > 0

                foundDoublePair && repeatingLetterWithOneInBetween
            }.count()

            println("Part 2: $niceRowsCount")
        }
        println("Time: $time2 ms")
    }

    private fun findDoublePair(row: String): Boolean {

        val found = mutableSetOf<Pair<String, Int>>()
        var previous = row.first()

        row.drop(1).forEachIndexed { i, c ->
            val pair = previous.toString().plus(c)

            val existingPair = found.firstOrNull { it.first == pair }
            if (existingPair != null && i - existingPair.second > 1) {
                return true
            }
            found.add(Pair(pair,i))
            previous = c
        }

       return false
    }
}