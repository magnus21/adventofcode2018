package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil.getBoundaries
import adventofcode.util.FileParser
import java.awt.Point
import kotlin.system.measureTimeMillis

object Day25 {

    @JvmStatic
    fun main(args: Array<String>) {

        val time = measureTimeMillis {
            val map = parseInput(FileParser.getFileRows(2021, "25.txt"))
            val boundaries = getBoundaries(map.keys.map { Pair(it.x, it.y) })
            var seaCucumberMap = map.filter { it.value != '.' }

            var step = 1
            while (true) {
                val afterEastsMoveMap = seaCucumberMap
                    .map {
                        if (it.value == '>') {
                            val nextX = if (it.key.x + 1 > boundaries.xmax) 0 else it.key.x + 1
                            val moveToPoint = Point(nextX, it.key.y)

                            if (seaCucumberMap.containsKey(moveToPoint)) Pair(it.key, it.value)
                            else Pair(moveToPoint, it.value)
                        } else {
                            Pair(it.key, it.value)
                        }
                    }.toMap()

                val afterSouthsMoveMap = afterEastsMoveMap
                    .map {
                        if (it.value == 'v') {
                            val nextY = if (it.key.y + 1 > boundaries.ymax) 0 else it.key.y + 1
                            val moveToPoint = Point(it.key.x, nextY)

                            if (afterEastsMoveMap.containsKey(moveToPoint)) Pair(it.key, it.value)
                            else Pair(moveToPoint, it.value)
                        } else {
                            Pair(it.key, it.value)
                        }
                    }.toMap()

                if (afterSouthsMoveMap == seaCucumberMap) {
                    break
                }

                seaCucumberMap = afterSouthsMoveMap
                step++
            }
            println("answer: $step")
        }
        println("Time: $time ms")
    }

    private fun parseInput(rows: List<String>): Map<Point, Char> {
        return rows.flatMapIndexed { y, row -> row.mapIndexed { x, c -> Pair(Point(x, y), c) } }.toMap()
    }
}