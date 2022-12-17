package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.manhattanDistance2D
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.time.ExperimentalTime

object Day15 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val sensors = parseInput(FileParser.getFileRows(2022, "15.txt"))

        printResult("part 1") { part1(sensors) }
        printResult("part 2") { part2(sensors) }
    }

    private fun part1(sensors: List<Sensor>): Int {
        val row = 2000000
        val noBeaconPositions = mutableSetOf<Point>()
        sensors.map { sensor ->
            val toBeaconLength = manhattanDistance2D(sensor.pos, sensor.closestBeacon)
            val length = abs(sensor.pos.y - row)

            if (length < toBeaconLength) {
                (0 until toBeaconLength - length).forEach { xd ->
                    noBeaconPositions.add(Point(sensor.pos.x + xd, row));
                    noBeaconPositions.add(Point(sensor.pos.x - xd, row));
                }
            }
        }

        val beaconOrSensorOnRow = sensors
            .flatMap { listOf(Point(it.closestBeacon.x, it.closestBeacon.y), Point(it.pos.x, it.pos.y)) }
            .filter { it.y == row }
            .toSet()
        noBeaconPositions.removeAll(beaconOrSensorOnRow)

        return noBeaconPositions.size
    }

    private fun part2(sensors: List<Sensor>): Long? {
        val range = 4000000L
        printMap100x100(sensors)

        val reachPerSensor = sensors.associate { Pair(it.pos, manhattanDistance2D(it.pos, it.closestBeacon)) }

        sensors.forEach { sensor ->
            val borderPoints = mutableSetOf<Point>()
            val reach = reachPerSensor[sensor.pos]!!

            (sensor.pos.x..sensor.pos.x + reach + 1).forEachIndexed { i, x ->
                borderPoints.add(Point(x, sensor.pos.y - reach - 1 + i))
                borderPoints.add(Point(x, sensor.pos.y + reach + 1 - i))
            }

            (sensor.pos.x downTo sensor.pos.x - reach - 1).forEachIndexed { i, x ->
                borderPoints.add(Point(x, sensor.pos.y - reach - 1 + i))
                borderPoints.add(Point(x, sensor.pos.y + reach + 1 - i))
            }

            val unReachable = borderPoints
                .filter { p -> sensors.all { manhattanDistance2D(it.pos, p) - reachPerSensor[it.pos]!! > 0 } }
                .firstOrNull { it.x >= 0 && it.y >= 0 && it.x <= range && it.y <= range }

            if(unReachable != null) {
                return unReachable.x.toLong() * range + unReachable.y
            }
        }
        return null
    }

    private fun printMap100x100(sensors: List<Sensor>) {
        val scaled = sensors.flatMap {
            listOf(
                Triple(
                    Pair(it.pos.x / 40000, it.pos.y / 40000),
                    's',
                    manhattanDistance2D(it.pos, it.closestBeacon) / 40000
                ),
                Triple(Pair(it.closestBeacon.x / 40000, it.closestBeacon.y / 40000), 'b', null)
            )
        }
        printMap(scaled, boundariesParam = AdventOfCodeUtil.Boundaries(0, 0, 100, 100))
    }

    private fun printMap(
        points: List<Triple<Pair<Int, Int>, Char, Int?>>,
        printBlanks: Boolean = true,
        blankChar: Char = '.',
        boundariesParam: AdventOfCodeUtil.Boundaries?
    ) {
        val boundaries = boundariesParam ?: AdventOfCodeUtil.getBoundaries(points.map { it.first }.toMutableSet())

        val pointsMap = points.associate { Pair(it.first, Pair(it.second, it.third)) }

        for (y in boundaries.ymin..boundaries.ymax) {
            for (x in boundaries.xmin..boundaries.xmax) {
                val point = pointsMap[Pair(x, y)]
                print(
                    if (point != null) {
                        if (point.first == 's') point.second!! else 'b'
                    } else if (pointsMap.filter { it.value.first == 's' }.any {
                            abs(
                                manhattanDistance2D(Point(it.key.first, it.key.second), Point(x, y))
                                        - it.value.second!!
                            ) < 1
                        }) {
                        pointsMap.filter { it.value.first == 's' }.filter {
                            abs(
                                manhattanDistance2D(Point(it.key.first, it.key.second), Point(x, y))
                                        - it.value.second!!
                            ) < 1
                        }.map { manhattanDistance2D(Point(it.key.first, it.key.second), Point(x, y)) }.first()
                        '+'
                    } else (if (printBlanks) blankChar else " ")
                )
            }
            println()
        }
    }

    private fun parseInput(rows: List<String>): List<Sensor> {
        return rows.map {
            "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
                .matchEntire(it)?.destructured
                ?.let { (sx, sy, bx, by) -> Sensor(Point(sx.toInt(), sy.toInt(), 0), Point(bx.toInt(), by.toInt(), 1)) }
                ?: throw IllegalArgumentException("Bad input '$it'")
        }
    }

    private data class Sensor(val pos: Point, val closestBeacon: Point)
}