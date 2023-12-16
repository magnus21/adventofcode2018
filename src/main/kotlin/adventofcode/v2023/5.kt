package adventofcode.v2023

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day5 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val (seeds, maps) = parseInput(FileParser.getFileRows(2023, "5.txt"))

        printResult("part 1") { part1(seeds, maps) }
        printResult("part 2") { part2() }
    }

    private fun part1(seeds: List<Long>, maps: List<Map>): Long {
        return seeds.minOf { seed ->
            var source = seed
            maps.forEach { map -> source = map.getMappedNumber(source) }
            source
        }
    }

    private fun part2(): Int {
        return 0
    }

    private fun parseInput(fileRows: List<String>): Pair<List<Long>, List<Map>> {

        val seeds = fileRows.first().substring("seeds: ".length).split(" ").map { it.toLong() }
        val maps = fileRows.drop(2)
            .fold(mutableListOf<Map>()) { acc, row ->
                when {
                    row.contains("map") -> acc.add(Map(row.split(" ")[0]))
                    row.isNotEmpty() -> {
                        val parts = row.split(" ").map { it.toLong() }
                        acc.last().ranges.add(MapRange(parts[0], parts[1], parts[2]))
                    }
                }
                acc
            }
        return Pair(seeds, maps)
    }

    data class Map(val name: String, val ranges: MutableList<MapRange> = mutableListOf()) {
        fun getMappedNumber(sourceNr: Long): Long {
            return ranges.firstNotNullOfOrNull { it.getMappedNumber(sourceNr) } ?: sourceNr
        }
    }

    data class MapRange(val destRangeStart: Long, val sourceRangeStart: Long, val length: Long) {
        fun getMappedNumber(sourceNr: Long): Long? {
            if (sourceNr >= sourceRangeStart && sourceNr <= sourceRangeStart + length) {
                return destRangeStart + (sourceNr - sourceRangeStart)
            }
            return null
        }
    }

}