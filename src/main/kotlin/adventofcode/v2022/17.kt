package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getBoundariesForPoints
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day17 {

    // ####
    private val minus = listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))

    //.#.
    //###
    //.#.
    private val plus = listOf(Point(0, 1), Point(1, 2), Point(1, 1), Point(1, 0), Point(2, 1))

    //..#
    //..#
    //###
    private val angle = listOf(Point(0, 0), Point(1, 0), Point(2, 2), Point(2, 1), Point(2, 0))

    //#
    //#
    //#
    //#
    private val pipe = listOf(Point(0, 3), Point(0, 2), Point(0, 1), Point(0, 0))

    //##
    //##
    private val cube = listOf(Point(0, 1), Point(0, 0), Point(1, 1), Point(1, 0))

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val jetPattern = FileParser.getAsString(2022, "17.txt").toCharArray()
        //val jetPattern = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>".toCharArray() // example
        printResult("part 1") { part1(jetPattern) }
        printResult("part 2") { part2(jetPattern) }
    }

    private fun part1(jetPattern: CharArray): Long {

        val rocks = listOf(minus, plus, angle, pipe, cube)

        val restingRocks = mutableSetOf<Point>()
        var restingRocksTopY = -1L
        var jetPatternIndex = 0

        val nrRocks = 2022
        (0 until nrRocks).forEach { i ->
            val rockType = rocks[(i % rocks.size)]
            var rock = moveToStartPos(rockType, restingRocksTopY)
            do {
                rock = jetPush(rock, jetPattern[jetPatternIndex % jetPattern.size], restingRocks)
                val (newRock, resting) = tryFalling(rock, restingRocks)
                rock = newRock

                jetPatternIndex++
            } while (!resting)
            restingRocks.addAll(rock)
            restingRocksTopY = getBoundariesForPoints(restingRocks).ymax.toLong()
        }
        /*
        println("\n")
        printPointsLeftDownOrigo(restingRocks.map { Pair(it.x,it.y) }.toSet(), printBlanks = true)
        println("-------")
         */

        return restingRocksTopY + 1L
    }

    private fun part2(jetPattern: CharArray): Long {

        val rocks = listOf(minus, plus, angle, pipe, cube)

        val restingRocks = mutableSetOf<Point>()
        var restingRocksTopY = -1L
        var jetPatternIndex = 0

        // From manual inspection of output (looking for repeating patterns).
        // Example
        /*
        val repeatingPatternStartTopY = 50L
        val repeatingPatternEndTopY = 103L
        val repeatingPatternHeight = repeatingPatternEndTopY - repeatingPatternStartTopY
        */

        // My input
        val repeatingPatternStartTopY = 1816L
        val repeatingPatternEndTopY = 2677L + 1816L
        val repeatingPatternHeight = repeatingPatternEndTopY - repeatingPatternStartTopY

        val nrRocks = 1000000000000L
        var i = 1L
        var fastForwardHeight = 0L
        var rockNrStartRepeatingPattern = 0L
        while (i <= nrRocks) {

            if (rockNrStartRepeatingPattern == 0L && repeatingPatternStartTopY < restingRocksTopY) {
                rockNrStartRepeatingPattern = i - 1
            }

            /*if (i % 10000 == 0L) {
                println("\n")
                printPointsLeftDownOrigo(restingRocks.map { Pair(it.x, it.y) }.toSet(), printBlanks = true)
                println("-------")
            }*/

            if (fastForwardHeight == 0L && repeatingPatternEndTopY < restingRocksTopY) {
                val rocksInRepeatingPattern = i - 1 - rockNrStartRepeatingPattern
                val fastForwardToRockRest = (nrRocks - i - 1) % rocksInRepeatingPattern
                fastForwardHeight = repeatingPatternHeight * ((nrRocks - i - 1) / rocksInRepeatingPattern)

                i = nrRocks - 1 - fastForwardToRockRest
            }

            val rockType = rocks[((i - 1) % rocks.size).toInt()]
            var rock = moveToStartPos(rockType, restingRocksTopY)
            do {
                rock = jetPush(rock, jetPattern[jetPatternIndex % jetPattern.size], restingRocks)
                val (newRock, resting) = tryFalling(rock, restingRocks)
                rock = newRock

                jetPatternIndex++
            } while (!resting)
            restingRocks.addAll(rock)
            restingRocksTopY = getBoundariesForPoints(restingRocks).ymax.toLong()
            i++
        }

        return restingRocksTopY + 1L + fastForwardHeight
    }

    private fun tryFalling(rock: List<Point>, restingRocks: MutableSet<Point>): Pair<List<Point>, Boolean> {
        val newRock = rock.map {
            Point(it.x, it.y - 1)
        }
        val bounds = getBoundariesForPoints(newRock)
        return if (newRock.toSet().intersect(restingRocks).isEmpty() && bounds.ymin >= 0) Pair(newRock, false)
        else Pair(rock, true)
    }

    private fun jetPush(rock: List<Point>, jetChar: Char, restingRocks: MutableSet<Point>): List<Point> {
        val direction = if (jetChar == '<') -1 else 1
        val newRock = rock.map {
            Point(it.x + direction, it.y)
        }
        val bounds = getBoundariesForPoints(newRock)
        return if (bounds.xmin >= 0 && bounds.xmax <= 6 && newRock.toSet().intersect(restingRocks).isEmpty()) newRock
        else rock
    }

    private fun moveToStartPos(
        rockType: List<Point>,
        restingRocksTopY: Long,
        startXPos: Int = 2,
        startYPosDistance: Int = 4
    ): List<Point> {
        return rockType.map {
            Point(it.x + startXPos, it.y + startYPosDistance + restingRocksTopY.toInt())
        }
    }
}