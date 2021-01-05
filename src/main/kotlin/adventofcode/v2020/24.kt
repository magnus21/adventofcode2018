package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {


        val input = FileParser.getFileRows(2020, "24.txt")
        val tilePaths = parseInput(input)

        val time = measureTimeMillis {
            val part1Result = part1(tilePaths)
            println("Part 1: ${part1Result.groupBy { it }.filter { it.value.size == 1 }.count()}")
            println("Part 2: ${part2(part1Result)}")
        }
        println("Time: ($time milliseconds)")
    }

    enum class Direction(val code: String, val x: Int, val y: Int) {
        EAST("e", 2, 0),
        NORTHEAST("ne", 1, 2),
        NORTHWEST("nw", -1, 2),
        WEST("w", -2, 0),
        SOUTHWEST("sw", -1, -2),
        SOUTHEAST("se", 1, -2);

        companion object {
            fun fromLabel(value: String): Direction {
                return when (value) {
                    "e" -> EAST
                    "ne" -> NORTHEAST
                    "nw" -> NORTHWEST
                    "w" -> WEST
                    "sw" -> SOUTHWEST
                    "se" -> SOUTHEAST
                    else -> throw Exception("Bad value: $value")
                }
            }
        }
    }

    private fun part1(tilePaths: List<List<Direction>>): List<Pair<Int, Int>> {

        return tilePaths.map { tilePath ->
            tilePath.fold(Pair(0, 0)) { pos, d ->
                Pair(pos.first + d.x, pos.second + d.y)
            }
        }
    }

    private fun part2(tilePositions: List<Pair<Int, Int>>): Int {
        var blackTiles = tilePositions
            .groupBy { it }
            .filter { it.value.size == 1 }
            .flatMap { it.value }
            .toSet()

        for (day in 1..100) {
            val allNeighboursToBlackTiles = blackTiles.flatMap { getHexGridNeighbours(it) }.toSet()

            blackTiles = allNeighboursToBlackTiles.plus(blackTiles).filter { tile ->
                val blackTileNeighboursCount = getHexGridNeighbours(tile)
                    .count { blackTiles.contains(it) }

                if (blackTiles.contains(tile)) !(blackTileNeighboursCount == 0 || blackTileNeighboursCount > 2)
                else blackTileNeighboursCount == 2

            }.toSet()

            // println("Day $day: ${blackTiles.size}")
        }


        return blackTiles.size
    }

    private fun getHexGridNeighbours(position: Pair<Int, Int>): List<Pair<Int, Int>> {
        return Direction.values().map { Pair(position.first + it.x, position.second + it.y) }
    }

    private fun parseInput(input: List<String>): List<List<Direction>> {
        return input.map { row ->
            row.fold(Pair(listOf<Direction>(), ' ')) { acc, c ->
                when {
                    (c == 's' || c == 'n') -> Pair(acc.first, c)
                    acc.second == 's' || acc.second == 'n' -> Pair(
                        acc.first.plus(Direction.fromLabel(acc.second + c.toString())),
                        c
                    )
                    else -> Pair(acc.first.plus(Direction.fromLabel(c.toString())), c)
                }
            }.first
        }
    }

}