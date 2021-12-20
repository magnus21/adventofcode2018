package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {
        val boundaries = parseInput(FileParser.getFileRows(2021, "17.txt"))

        val time1 = measureTimeMillis {
            // Natural number series sum, what goes up must come down.
            val maxY = (boundaries.ymax - 1) * ((boundaries.ymax - 1) + 1) / 2
            println("answer part 1: $maxY")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val possibleXVelocities = findAllPossibleXVelocities(boundaries.xmin, boundaries.xmax)
            val possibleYVelocities = findAllPossibleYVelocities(boundaries.ymin, boundaries.ymax)

            var count = 0
            for (vx in possibleXVelocities) {
                for (vy in possibleYVelocities) {
                    if (xyWillHitTargetArea(vx, vy, boundaries)) {
                        count++
                    }
                }
            }

            println("answer part 2: $count")
        }
        println("Time: $time2 ms")
    }

    private fun findAllPossibleXVelocities(min: Int, max: Int): Set<Int> {
        val possibleVelocities = mutableSetOf<Int>()
        for (vyStart in 0 until min) {
            if (willHitXBoundary(vyStart, min, max)) {
                possibleVelocities.add(vyStart)
            }
        }
        possibleVelocities.addAll((min..max))

        return possibleVelocities
    }

    private fun findAllPossibleYVelocities(min: Int, max: Int): Set<Int> {
        val possibleVelocities = mutableSetOf<Int>()
        for (vyStart in 0 until min) {
            if (willHitYBoundary(vyStart, min, max)) {
                possibleVelocities.add(vyStart)
            }
        }
        possibleVelocities.addAll(possibleVelocities.map { -it - 1 })

        possibleVelocities.addAll((min - 1) until max)
        possibleVelocities.addAll((min..max).map { -it })

        return possibleVelocities
    }

    private fun willHitXBoundary(vxStart: Int, min: Int, max: Int): Boolean {
        var x = 0
        var vx = vxStart
        while (x <= min && vx > 0) {
            x += vx
            if (x in min..max) {
                return true
            } else if (x > max) {
                return false
            }
            vx--
        }
        return false
    }

    private fun willHitYBoundary(vyStart: Int, min: Int, max: Int): Boolean {
        var y = 0
        var vy = vyStart
        while (y <= min) {
            y += vy
            if (y in min..max) {
                return true
            } else if (y > max) {
                return false
            }
            vy++
        }
        return false
    }

    private fun xyWillHitTargetArea(vxStart: Int, vyStart: Int, boundaries: AdventOfCodeUtil.Boundaries): Boolean {
        var x = 0
        var y = 0
        var vx = vxStart
        var vy = vyStart
        while (y > -boundaries.ymax || vx > 0) {
            x += vx
            y += vy
            if (y in -boundaries.ymax..-boundaries.ymin && x in boundaries.xmin..boundaries.xmax) {
                return true
            }
            if (vx > 0) {
                vx--
            }
            vy--
        }
        return false
    }

    private fun parseInput(fileRows: List<String>): AdventOfCodeUtil.Boundaries {
        val regex = """target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""".toRegex()
        val (xmin, xmax, ymin, ymax) = regex.find(fileRows.first())!!.destructured

        return AdventOfCodeUtil.Boundaries(xmin.toInt(), -ymax.toInt(), xmax.toInt(), -ymin.toInt())
    }
}

/*
target area: x=20..30, y=-10..-5
S..............................................................
...............................................................
...............................................................
...............................................................
.................#.............................................
....................TTTTTTTTTTT................................
....................TTTTTTTTTTT................................
....................TTTTTTTTTTT................................
....................TTTTTTTTTTT................................
....................TTTTTTTTTTT..#.............................
....................TTTTTTTTTTT................................
...............................................................
...............................................................
...............................................................
...............................................................
................................................#..............
...............................................................
...............................................................
...............................................................
...............................................................
...............................................................
...............................................................
..............................................................#
 */