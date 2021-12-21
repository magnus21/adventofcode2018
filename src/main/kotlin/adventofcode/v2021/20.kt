package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {
        val (enhancementMask, startImage) = parseInput(FileParser.getFileRows(2021, "20.txt"))

        val firstMaskBit = enhancementMask.first()
        val lastMaskBit = enhancementMask.last()
        assert(firstMaskBit != lastMaskBit)

        val infinityPointsOn = { firstBit: Int, i: Int -> firstBit == 1 && i % 2 == 0 }
        val time1 = measureTimeMillis {
            var image = startImage.toMutableSet()
            for (i in 1..2) {
                image = enhanceImage(image, enhancementMask, infinityPointsOn.invoke(firstMaskBit, i))
                printImage(image)
            }
            println("answer part 1: ${image.size}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            var image = startImage.toMutableSet()
            for (i in 1..50) {
                image = enhanceImage(image, enhancementMask, infinityPointsOn.invoke(firstMaskBit, i))
            }
            println("answer part 2: ${image.size}")
        }
        println("Time: $time2 ms")
    }

    private fun enhanceImage(
        image: MutableSet<Point>,
        enhancementMask: List<Int>,
        infinityPointsOn: Boolean
    ): MutableSet<Point> {
        val result = mutableSetOf<Point>()
        val boundaries = AdventOfCodeUtil.getBoundaries(image.map { Pair(it.x, it.y) })

        for (yy in boundaries.ymin - 1..boundaries.ymax + 1) {
            for (xx in boundaries.xmin - 1..boundaries.xmax + 1) {
                val point = Point(xx, yy)

                val decimalNumber = Integer.parseInt(
                    (point.y - 1..point.y + 1).flatMap { y ->
                        (point.x - 1..point.x + 1).map { x ->
                            if (image.contains(Point(x, y)) || (infinityPointsOn && infinityPoint(x, y, boundaries))) 1
                            else 0
                        }
                    }.joinToString(""),
                    2
                )
                if (enhancementMask[decimalNumber] == 1) {
                    result.add(point)
                }
            }
        }

        return result
    }

    private fun infinityPoint(x: Int, y: Int, boundaries: AdventOfCodeUtil.Boundaries): Boolean {
        return x < boundaries.xmin || x > boundaries.xmax ||
                y < boundaries.ymin || y > boundaries.ymax
    }

    private fun parseInput(rows: List<String>): Pair<List<Int>, Set<Point>> {
        val enhancementMask = rows.first().map { if (it == '#') 1 else 0 }

        val image = mutableSetOf<Point>()
        rows.drop(2).mapIndexed { y, row ->
            row.mapIndexed { x, c ->
                if (c == '#') {
                    image.add(Point(x, y))
                }
            }
        }

        return Pair(enhancementMask, image)
    }

    private fun printImage(image: Set<Point>) {
        println("================================================================")
        val boundaries = AdventOfCodeUtil.getBoundaries(image.map { Pair(it.x, it.y) })
        for (yy in boundaries.xmin..boundaries.xmax) {
            val row = (boundaries.ymin..boundaries.ymax).map { xx ->
                if (image.contains(Point(xx, yy))) '#' else '.'
            }.joinToString("")
            println(row)
        }
    }
}