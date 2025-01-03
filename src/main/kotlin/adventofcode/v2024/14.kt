package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day14 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val robots = parseInput(FileParser.getFileRows(2024, "14.txt"))

        printResult("part 1") { solve(robots, 101, 103, 100) }
        printResult("part 2") { solve2(robots, 101, 103) }
    }

    private fun solve2(robots: List<Robot>, xSize: Long, ySize: Long): Int {
        var mutableRobots = robots
        var i = 0
        do {
            i++
            mutableRobots = mutableRobots.map { r ->
                val x = (r.x + r.xStep) % xSize
                val y = (r.y + r.yStep) % ySize
                Robot(if (x < 0) xSize + x else x, if (y < 0) ySize + y else y, r.xStep, r.yStep)
            }
        } while (!isChristmasTree(mutableRobots))

        printRobots(xSize, ySize, mutableRobots.map { Pair(it.x, it.y) }.toSet())

        return i
    }

    private fun isChristmasTree(robots: List<Robot>): Boolean {
        // Check for continuous lines.
        return robots.groupBy { Pair(it.x / 10, it.y) }.maxOf { it.value.size } > 9
    }

    private fun printRobots(xSize: Long, ySize: Long, robots: Set<Pair<Long, Long>>) {
        (0 until ySize).forEach { y ->
            val line = (0 until xSize).map { x ->
                if (robots.contains(Pair(x, y))) '#' else '.'
            }.joinToString("")
            println(line)
        }
    }

    private fun solve(robots: List<Robot>, xSize: Long, ySize: Long, seconds: Long): Long {

        val robotEndPositions = robots.map { robot ->
            Pair(
                (robot.x + (robot.xStep * seconds) % xSize) % xSize,
                (robot.y + (robot.yStep * seconds) % ySize) % ySize
            )
        }.map {
            Pair(
                if (it.first < 0) xSize + it.first else it.first,
                if (it.second < 0) ySize + it.second else it.second
            )
        }

        val robotEndPositionsInQuadrants =
            robotEndPositions
                .filter { it.first != xSize / 2 && it.second != ySize / 2 }
                .groupBy { Pair(it.first < xSize / 2, it.second < ySize / 2) }
        return robotEndPositionsInQuadrants.map { it.value.size.toLong() }.reduce { acc, i -> acc * i }
    }

    data class Robot(val x: Long, val y: Long, val xStep: Int, val yStep: Int)

    private fun parseInput(input: List<String>): List<Robot> {
        return input.map { robot ->
            "p=(\\d+),(\\d+)\\sv=(-?\\d+),(-?\\d+)".toRegex().findAll(robot).map {
                Robot(
                    it.groups[1]!!.value.toLong(),
                    it.groups[2]!!.value.toLong(),
                    it.groups[3]!!.value.toInt(),
                    it.groups[4]!!.value.toInt()
                )
            }.first()
        }
    }
}