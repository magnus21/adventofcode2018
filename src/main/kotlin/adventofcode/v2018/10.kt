package adventofcode.v2018

import adventofcode.util.AdventOfCodeUtil.Boundaries
import java.io.File

fun main(args: Array<String>) {

    val points = File("src/main/resources/10.txt").readLines().map { parsePoint(it) }

    val letterHeightMargin = 12 // A good guess, points should converge since they never change direction.
    var seconds = 0
    do {
        val boundaries = getBoundaries(points)
        if (boundaries.ymax - boundaries.ymin <= letterHeightMargin) {
            printPoints(boundaries, points)
            break
        }

        stepOneSecond(points)
        seconds++
    } while (seconds < 1000000000)

    println("Seconds: $seconds")
}

private fun printPoints(boundaries: Boundaries, points: List<Point>) {
    for (y in boundaries.ymin..boundaries.ymax) {
        for (x in boundaries.xmin..boundaries.xmax) {
            print(if (points.any { p -> p.x == x && p.y == y }) "#" else ".")
        }
        println()
    }
}

fun stepOneSecond(points: List<Point>) {
    points.forEach { point ->
        point.x += point.xVelocity
        point.y += point.yVelocity
    }
}

fun parsePoint(pointsString: String): Point {
    val parts = pointsString
        .replace("position=<", "")
        .replace("> velocity=<", ",")
        .replace(">", "")
        .split(",")
        .map { Integer.valueOf(it.trim()) }

    return Point(parts[0], parts[1], parts[2], parts[3])
}

data class Point(var x: Int, var y: Int, val xVelocity: Int, val yVelocity: Int)

fun getBoundaries(points: List<Point>): Boundaries {
    return points.fold(Boundaries(10000, 100000, -1, -1)) { bounds, point ->
        if (point.x < bounds.xmin)
            bounds.xmin = point.x
        if (point.y < bounds.ymin)
            bounds.ymin = point.y
        if (point.x > bounds.xmax)
            bounds.xmax = point.x
        if (point.y > bounds.ymax)
            bounds.ymax = point.y

        bounds
    }
}