package adventofcode.v2018

import java.io.File

fun main(args: Array<String>) {
    Day25.start()
}

object Day25 {
    data class Point(val x: Int, val y: Int, val z: Int, val time: Int)

    fun start() {
        solve(parseInput(File("src/main/resources/25.txt").readLines()))
    }

    private fun solve(points: List<Point>) {
        val constellations = mutableListOf<MutableSet<Point>>()

        for (point in points) {
            val connectedConstellations =
                constellations
                    .filter { c -> c.any { manhattanDistance(point, it) <= 3 } }
                    .toMutableList()

            if (connectedConstellations.isEmpty()) {
                val connectedPoints = points
                    .filter { it != point }
                    .filter { manhattanDistance(point, it) <= 3 }
                    .toMutableSet()

                connectConstellations(point, mutableListOf(connectedPoints), constellations)
            } else {
                connectConstellations(point, connectedConstellations, constellations)
            }
        }

        println(constellations.size)
    }

    private fun connectConstellations(
        point: Point,
        connectedConstellations: MutableList<MutableSet<Point>>,
        constellations: MutableList<MutableSet<Point>>
    ) {
        val newConstellation = connectedConstellations.flatten().toMutableSet()
        newConstellation.add(point)

        constellations.removeAll(connectedConstellations)
        constellations.add(newConstellation)
    }

    private fun manhattanDistance(point1: Point, point2: Point): Int {
        return Math.abs(point1.x - point2.x) +
                Math.abs(point1.y - point2.y) +
                Math.abs(point1.z - point2.z) +
                Math.abs(point1.time - point2.time)
    }

    private fun parseInput(input: List<String>): List<Point> {
        return input
            .map {
                val parts = it.split(",").map { Integer.valueOf(it) }
                Point(parts[0], parts[1], parts[2], parts[3])
            }
    }

}
