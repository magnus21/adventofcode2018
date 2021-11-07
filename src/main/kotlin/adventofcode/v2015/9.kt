package adventofcode.v2015

import adventofcode.util.FileParser
import java.util.*
import kotlin.system.measureTimeMillis

object Day9 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2015, "9.txt")

        val distances = parseInput(input)
        val locations = distances.flatMap { listOf(it.first, it.second) }.distinct()

        val neighbours = locations
            .map { loc ->
                Pair(
                    loc,
                    distances.filter { d -> d.first == loc || d.second == loc }.map {
                        Pair(listOf(it.second, it.first).first { it != loc }, it.third)
                    }
                )
            }
            .toMap()

        val time1 = measureTimeMillis {

            val result = locations.map { loc ->
                findPath(loc, locations, neighbours, { _, _ -> false }, compareBy { it.distance })
            }.filterNotNull().minByOrNull { it.distance }!!

            println("Part 1: ${result.distance}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            val result = locations.map { loc ->
                findPath(
                    loc,
                    locations,
                    neighbours,
                    { distance, path -> distance < path.distance },
                    compareByDescending { it.distance }
                )
            }.filterNotNull().maxByOrNull { it.distance }!!

            println("Part 2: ${result.distance}")
        }
        println("Time: $time2 ms")
    }

    private fun findPath(
        loc: String,
        locations: List<String>,
        neighbours: Map<String, List<Pair<String, Int>>>,
        customStateCondition: (Int, Path) -> Boolean,
        comparator: Comparator<Path>
    ): Path? {
        val pathQueue = PriorityQueue<Path>(comparator)

        val reachedLocations = mutableMapOf<Pair<Set<String>, String>, Int>()

        pathQueue.add(Path(setOf(loc), loc, 0))
        reachedLocations[Pair(setOf(loc), loc)] = 0

        while (pathQueue.isNotEmpty()) {
            val path = pathQueue.remove()!!

            if (path.visited.size == locations.size) {
                return path
            }
            val headNeighbours = neighbours[path.head]!!

            for (n in headNeighbours) {
                if (!path.visited.contains(n.first)) {
                    val newPath = Path(path.visited.plus(n.first), n.first, path.distance + n.second)
                    val state = Pair(newPath.visited, newPath.head)

                    val reachedStateDistance = reachedLocations[state]
                    if (reachedStateDistance == null || customStateCondition(reachedStateDistance, newPath)) {
                        pathQueue.add(newPath)
                        reachedLocations[state] = newPath.distance
                    }
                }
            }
        }
        return null
    }

    data class Path(val visited: Set<String>, var head: String, var distance: Int)

    private fun parseInput(input: List<String>): List<Triple<String, String, Int>> {
        return input.map {
            val parts = it.split(' ')
            Triple(parts[0], parts[2], parts[4].toInt())
        }
    }
}