package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getPerpendicularNeighbours2d
import adventofcode.util.AdventOfCodeUtil.manhattanDistance2D
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day12 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val gardenMap = parseInput(FileParser.getFileRows(2024, "12.txt"))

        printResult("part 1") { solve(gardenMap) }
        printResult("part 2") { solve(gardenMap, true) }
    }

    private fun solve(gardenMap: Map<Point, Char>, bulkDiscount: Boolean = false): Long {
        val processed = mutableSetOf<Point>()
        return gardenMap
            .mapNotNull { plot ->
                if (!processed.contains(plot.key)) {
                    val processedInRegion = mutableSetOf<Point>()
                    computeRegion(Pair(plot.key, plot.value), gardenMap, processedInRegion)
                    processed.addAll(processedInRegion)

                    Pair(plot.value, processedInRegion)
                } else null
            }.sumOf { calculateFenceCost(it, bulkDiscount, gardenMap) }
    }

    private fun computeRegion(
        plot: Pair<Point, Char?>,
        gardenMap: Map<Point, Char>,
        processed: MutableSet<Point>
    ) {
        processed.add(plot.first)
        getPerpendicularNeighbours2d(plot.first.x, plot.first.y)
            .map { Pair(Point(it.first, it.second), gardenMap[Point(it.first, it.second)]) }
            .filter { it.second != null && it.second == plot.second && !processed.contains(it.first) }
            .forEach { computeRegion(it, gardenMap, processed) }
    }

    private fun calculateFenceCost(
        region: Pair<Char, Set<Point>>,
        bulkDiscount: Boolean,
        gardenMap: Map<Point, Char>
    ): Long {
        val area = region.second.size.toLong()
        val regionMap = region.second.toSet()
        val multiplier =
            if (bulkDiscount) calculateRegionSides(regionMap, gardenMap, region.first)
            else region.second.sumOf { getPerimeterCount(it, regionMap) }

        return area * multiplier
    }

    private fun calculateRegionSides(regionMap: Set<Point>, gardenMap: Map<Point, Char>, plotCode: Char): Int {
        val borderNeighboursMap = regionMap.map { plot ->
            Pair(
                plot,
                getPerpendicularNeighbours2d(plot.x, plot.y).filter {
                    val neighbour = gardenMap[Point(it.first, it.second)]
                    neighbour == null || neighbour != plotCode
                })
        }
        val borderPlots = borderNeighboursMap
            .filter { it.second.isNotEmpty() }
            .map { Pair(it.first, it.second.map { neighbour -> getDirection(it.first, neighbour) }) }

        val sides = mutableListOf<Pair<Char, MutableSet<Point>>>()
        borderPlots.sortedWith(compareBy({ it.first.x }, { it.first.y }))
            .forEach { borderPlot ->
                val matchingSides = sides.filter { side ->
                    side.second.any { manhattanDistance2D(it, borderPlot.first) == 1 } &&
                            borderPlot.second.contains(side.first)
                }

                val matchingDirections = matchingSides.map { it.first }
                val missingDirections = borderPlot.second.filter { !matchingDirections.contains(it) }

                missingDirections.forEach { direction -> sides.add(Pair(direction, mutableSetOf(borderPlot.first))) }
                matchingSides.forEach { it.second.add(borderPlot.first) }
            }

        return sides.size
    }

    private fun getDirection(borderPoint: Point, neighbour: Pair<Int, Int>): Char {
        val xDiff = borderPoint.x - neighbour.first
        val yDiff = borderPoint.y - neighbour.second

        return when {
            xDiff == 1 -> 'L'
            xDiff == -1 -> 'R'
            yDiff == 1 -> 'D'
            else -> 'U'
        }
    }

    private fun getPerimeterCount(point: Point, regionMap: Set<Point>) =
        getPerpendicularNeighbours2d(point.x, point.y).count { !regionMap.contains(Point(it.first, it.second)) }

    private fun parseInput(input: List<String>) =
        input.flatMapIndexed { y, row -> row.toCharArray().mapIndexed { x, c -> Pair(Point(x, y), c) } }.toMap()
}