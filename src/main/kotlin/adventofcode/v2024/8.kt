package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.generatePairs
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day8 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val rawMap = parseInput(FileParser.getFileRows(2024, "8.txt"))

        printResult("part 1") { solve(rawMap, false) }
        printResult("part 2") { solve(rawMap, true) }
    }

    private fun solve(rawMap: List<Pair<Point, Char>>, useResonantHarmonics: Boolean): Int {
        val map = rawMap.toMap()
        val groupedByAntennaType = rawMap.filter { it.second != '.' }.groupBy { it.second }.values
        val antiNodes = groupedByAntennaType.flatMap { antennaGroup ->
            getPairs(antennaGroup).flatMap { getAntiNodesInsideMap(it, map, useResonantHarmonics) }
        }
        return antiNodes.toSet().size
    }

    private fun getAntiNodesInsideMap(
        antennaPair: AntennaPair,
        map: Map<Point, Char>,
        useResonantHarmonics: Boolean
    ): List<Point> {
        val diff = getPairDistanceDiff(antennaPair)
        return if (useResonantHarmonics) {
            val listAntenna1 = getAntiNodesPerAntenna(antennaPair.antenna1.first, diff, map, 1)
            val listAntenna2 = getAntiNodesPerAntenna(antennaPair.antenna2.first, diff, map, -1)

            listAntenna1.union(listAntenna2).toList()
        } else listOf(
            Point(antennaPair.antenna1.first.x + diff.x, antennaPair.antenna1.first.y + diff.y),
            Point(antennaPair.antenna2.first.x - diff.x, antennaPair.antenna2.first.y - diff.y)
        ).filter { map.containsKey(Point(it.x, it.y)) }
    }

    private fun getAntiNodesPerAntenna(
        antenna: Point,
        diff: Point,
        map: Map<Point, Char>,
        sign: Int
    ): MutableList<Point> {
        var step = 0
        val list = mutableListOf<Point>()
        do {
            list.add(Point(antenna.x + sign * diff.x * step, antenna.y + sign * diff.y * step++))
            val nextPos =
                Point(antenna.x + sign * diff.x * step, antenna.y + sign * diff.y * step)
        } while (map.containsKey(nextPos))
        return list
    }

    private fun getPairs(antennaGroup: List<Pair<Point, Char>>) =
        generatePairs(antennaGroup).map { AntennaPair(it.first, it.second) }

    private fun getPairDistanceDiff(antennaPair: AntennaPair) =
        Point(
            antennaPair.antenna1.first.x - antennaPair.antenna2.first.x,
            antennaPair.antenna1.first.y - antennaPair.antenna2.first.y
        )

    data class AntennaPair(val antenna1: Pair<Point, Char>, val antenna2: Pair<Point, Char>)

    private fun parseInput(input: List<String>) =
        input.flatMapIndexed { y, row -> row.toCharArray().mapIndexed { x, c -> Pair(Point(x, y), c) } }
}