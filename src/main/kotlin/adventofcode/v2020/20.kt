package adventofcode.v2020

import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2020.Day20.TileBorder.*
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "20.txt")
        val tiles = parseInput(input)

        val time = measureTimeMillis {
            println("Part 1: ${part1(tiles)}")
            println("Part 2: ${part2(tiles)}")
        }
        println("Time: ($time milliseconds)")
    }

    data class Tile(
        val index: Int,
        val value: MutableList<String> = mutableListOf(),
        val borders: MutableMap<TileBorder, String> = mutableMapOf(),
        val matchedBorders: MutableList<TileMatch> = mutableListOf()
    )

    enum class TileBorder {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    data class TileMatch(
        val border: TileBorder,
        val matchingTile: Tile,
        val matchingBorder: TileBorder,
        val reversed: Boolean
    )

    data class TilePos(val x: Int, val y: Int)

    private fun part1(tiles: List<Tile>): Long {

        val matchMap = tiles.map {
            Pair(it, getMatches(it, tiles))
        }.sortedBy { it.second.size }


        // Corners have only 2 matches, assuming (correctly) that alla matches/borders are unique.
        // 2251 3931 2113 2411
        return matchMap.filter { it.second.size == 2 }.map { it.first.index.toLong() }.reduce { acc, i -> acc * i }
    }

    private fun part2(tiles: List<Tile>): Long {
        val matchMap = tiles.map {
            Pair(it, getMatches(it, tiles))
        }

        matchMap.groupBy { it.second.size }.forEach {
            println("${it.key}: ${it.value.map { it.first.index }.joinToString(" ")}")
        }

        val squareSize = sqrt(tiles.size.toDouble()).toInt()

        // "Upper left" corner, cause I say so...
        val firstTile = matchMap
            .filter { it.second.size == 2 }
            .first { listOf(EAST, SOUTH).containsAll(listOf(it.second[0].border, it.second[1].border)) }

        val positions = findPositions(firstTile, squareSize, tiles)


        val pixels = mergeTiles(positions, squareSize, 10)
        printImage(pixels)

        val seaMonster = listOf(
            "                  # ",
            "#    ##    ##    ###",
            " #  #  #  #  #  #   "
        )
        val (seaMonsters, transformedPixels) = findNrOfSeaMonsters(seaMonster, pixels)

        println("Nr of sea monsters found: ${seaMonsters.size}")
        seaMonsters.forEach {
            println("At $it")
        }
        printImage(transformedPixels)

        return pixels.map { row -> row.toCharArray().count { it == '#' }.toLong() }.sum() -
                seaMonsters.size * seaMonster.map { row -> row.toCharArray().count { it == '#' }.toLong() }.sum()
    }

    private fun findNrOfSeaMonsters(
        seaMonster: List<String>,
        pixels: List<String>
    ): Pair<MutableList<Pair<Int, Int>>, MutableList<String>> {
        var transformedPixels = pixels.toMutableList()

        val seaMonsters = mutableListOf<Pair<Int, Int>>()
        while (true) {
            if (matchSeaMonsterAgainstPixels(pixels, seaMonster, transformedPixels, seaMonsters)) return Pair(
                seaMonsters,
                transformedPixels
            )

            val transformedAndFlippedPixels = flipTileX(transformedPixels)
            if (matchSeaMonsterAgainstPixels(pixels, seaMonster, transformedAndFlippedPixels, seaMonsters)) return Pair(
                seaMonsters,
                transformedAndFlippedPixels
            )

            transformedPixels = rotatePlus90(transformedPixels)
        }
    }

    private fun matchSeaMonsterAgainstPixels(
        pixels: List<String>,
        seaMonster: List<String>,
        transformedPixels: MutableList<String>,
        seaMonsters: MutableList<Pair<Int, Int>>
    ): Boolean {
        for (y in 0..(pixels.size - seaMonster.size)) {
            for (x in 0..(pixels.size - seaMonster.first().length)) {
                if (matchSeaMonster(x, y, seaMonster, transformedPixels)) {
                    seaMonsters.add(Pair(x, y))
                }
            }
        }
        if (seaMonsters.size > 0) {
            return true
        }
        return false
    }

    private fun matchSeaMonster(
        x: Int,
        y: Int,
        seaMonster: List<String>,
        transformedPixels: MutableList<String>
    ): Boolean {
        for (yy in y until (y + seaMonster.size)) {
            for (xx in x until (x + seaMonster.first().length)) {
                if (seaMonster[yy - y][xx - x] == '#' && transformedPixels[yy][xx] != '#') {
                    return false
                }

            }
        }
        return true
    }

    private fun findPositions(
        firstTile: Pair<Tile, List<TileMatch>>,
        squareSize: Int,
        tiles: List<Tile>
    ): MutableMap<TilePos, Tile> {
        val positions = mutableMapOf<TilePos, Tile>()
        positions[TilePos(0, 0)] = firstTile.first

        val queue = Queue<Pair<TilePos, Tile>>()

        queue.enqueue(Pair(TilePos(0, 0), firstTile.first))

        while (queue.isNotEmpty()) {
            val tile = queue.dequeue()!!
            val tilePos = tile.first

            val matches = getMatches(tile.second, tiles)
            positions[tilePos]!!.matchedBorders.addAll(matches);

            //printPositions(positions, squareSize);

            val newBorders = matches
                // Only EAST and SOUTH should be needed..
                .filter { listOf(EAST, SOUTH).contains(it.border) }
                .filter { positionIsNotFilled(tilePos, it, positions) }

            newBorders
                .forEach {
                    val transformedTileValue = when (it.border) {
                        EAST -> {
                            val maybeReversedTile =
                                if (it.reversed) flipTileY(it.matchingTile.value) else it.matchingTile.value
                            when (it.matchingBorder) {
                                WEST -> maybeReversedTile
                                EAST -> flipTileX(maybeReversedTile)
                                SOUTH -> if (it.reversed) flipTileY(rotatePlus90(it.matchingTile.value)) else rotatePlus90(
                                    it.matchingTile.value
                                )
                                else -> if (!it.reversed) flipTileY(rotateMinus90(it.matchingTile.value)) else rotateMinus90(
                                    it.matchingTile.value
                                )
                            }
                        }
                        // SOUTH
                        else -> {
                            val maybeReversedTile =
                                if (it.reversed) flipTileX(it.matchingTile.value) else it.matchingTile.value
                            when (it.matchingBorder) {
                                NORTH -> maybeReversedTile
                                SOUTH -> flipTileY(maybeReversedTile)
                                WEST -> if (!it.reversed) flipTileX(rotatePlus90(it.matchingTile.value)) else rotatePlus90(
                                    it.matchingTile.value
                                )
                                else -> if (it.reversed) flipTileX(rotateMinus90(it.matchingTile.value)) else rotateMinus90(
                                    it.matchingTile.value
                                )
                            }
                        }
                    }

                    val tilePosForTransformedTile =
                        if (it.border == EAST) TilePos(tilePos.x + 1, tilePos.y) else TilePos(tilePos.x, tilePos.y + 1)
                    val transformedTile =
                        Tile(it.matchingTile.index, transformedTileValue, getBorders(transformedTileValue))

                    positions[tilePosForTransformedTile] = transformedTile
                    queue.enqueue(Pair(tilePosForTransformedTile, transformedTile))
                }
        }

        printPositions(positions, squareSize);

        return positions
    }

    private fun printImage(pixels: List<String>) {
        println("==================================")
        pixels.forEach { println(it) }
        println("==================================")
    }


    private fun printPositions(
        positions: MutableMap<TilePos, Tile>,
        squareSize: Int
    ) {
        println("==================================")
        (0 until squareSize).forEach { row ->
            val str = (0 until squareSize * 2).map { col ->
                if (positions[TilePos(col, row)] != null) positions[TilePos(
                    col,
                    row
                )]!!.index.toString().plus(" ") else "     "
            }
                .joinToString("")
            println(str)
        }
        println("==================================")
    }

    private fun mergeTiles(
        positions: MutableMap<TilePos, Tile>,
        squareSize: Int,
        tileSize: Int
    ): List<String> {
        // Drop borders.
        return (0 until squareSize).flatMap { y ->
            val rowTiles = positions
                .filter { it.key.y == y }
                .entries.sortedBy { it.key.x }

            (1 until tileSize - 1).map { x ->
                rowTiles.joinToString("") { it.value.value.drop(x).take(1).joinToString("").drop(1).dropLast(1) }
            }
        }
    }

    private fun rotateMinus90(tileValue: MutableList<String>): MutableList<String> {
        return tileValue
            .mapIndexed { i, _ -> tileValue.map { it[tileValue.size - 1 - i] }.joinToString("") }
            .toMutableList()
    }

    private fun rotatePlus90(tileValue: MutableList<String>): MutableList<String> {
        return tileValue
            .mapIndexed { i, _ -> tileValue.map { it[i] }.joinToString("").reversed() }
            .toMutableList()
    }

    private fun flipTileX(tileValue: MutableList<String>): MutableList<String> {
        return tileValue.map { it.reversed() }.toMutableList()
    }

    private fun flipTileY(tileValue: MutableList<String>): MutableList<String> {
        return tileValue.reversed().toMutableList()
    }

    private fun positionIsNotFilled(
        tilePos: TilePos,
        tileMatch: TileMatch,
        positions: MutableMap<TilePos, Tile>
    ): Boolean {
        return when (tileMatch.border) {
            EAST -> positions[TilePos(tilePos.x + 1, tilePos.y)] == null
            else -> positions[TilePos(tilePos.x, tilePos.y + 1)] == null
        }
    }

    private fun getMatches(tile: Tile, tiles: List<Tile>): List<TileMatch> {
        return tile.borders.flatMap { tb ->
            tiles.filter { it.index != tile.index }.flatMap {
                it.borders.filter { b -> b.value == tb.value || b.value.reversed() == tb.value }
                    .map { b -> TileMatch(tb.key, it, b.key, b.value.reversed() == tb.value) }
            }
        }
    }

    private fun parseInput(input: List<String>): List<Tile> {
        return input.plus("").fold(mutableListOf()) { res, line ->
            when {
                line.startsWith("Tile") -> res.add(Tile(line.replace(":", "").split(" ")[1].toInt()))
                line == "" -> {
                    res.last().borders.putAll(getBorders(res.last().value))
                }
                else -> {
                    res.last().value.add(line)
                }
            }
            res
        }
    }

    private fun getBorders(tileValue: MutableList<String>): MutableMap<TileBorder, String> {
        val borders = mutableMapOf<TileBorder, String>()
        borders[NORTH] = tileValue.first()
        borders[EAST] = tileValue.map { it.last() }.joinToString("")
        borders[WEST] = tileValue.map { it.first() }.joinToString("")
        borders[SOUTH] = tileValue.last()
        return borders
    }
}