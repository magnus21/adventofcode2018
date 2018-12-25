package adventofcode

import adventofcode.RegionType.*

fun main(args: Array<String>) {

    val depth = 11394
    val targetPosition = Position(7, 701)

    //val depth = 510
    //val targetPosition = Position(10, 10)

    val startPosition = Position(0, 0)
    val map = mutableMapOf<Position, Region>()

    Day22.createMap(depth, targetPosition, startPosition, map)

    Day22.printMap(map, targetPosition, startPosition)

    val risklevel = Day22.calculateRiskLevelFor(map, startPosition, targetPosition)

    println("Risk level: $risklevel")
}

enum class RegionType(val code: Char, val riskLevel: Int) {
    ROCKY('.', 0),
    WET('=', 1),
    NARROW('|', 2)
}

data class Region(val position: Position, val geologicalIndex: Int, val erosionLevel: Int, val type: RegionType)

object Day22 {

    fun createMap(depth: Int, targetPosition: Position, startPosition: Position, map: MutableMap<Position, Region>) {

        for (y in startPosition.y..targetPosition.y) {
            for (x in startPosition.x..targetPosition.x) {
                val position = Position(x, y)

                val geologicalIndex = when {
                    position == startPosition -> 0
                    position == targetPosition -> 0
                    position.y == 0 -> position.x * 16807
                    position.x == 0 -> position.y * 48271
                    else -> map[Position(position.x - 1, position.y)]!!.erosionLevel *
                            map[Position(position.x, position.y - 1)]!!.erosionLevel
                }

                val erosionLevel = (geologicalIndex + depth) % 20183
                val type = getRegionType(erosionLevel % 3)

                map.put(position, Region(position, geologicalIndex, erosionLevel, type))
            }
        }
    }

    private fun getRegionType(riskLevel: Int): RegionType {
        return RegionType.values().first { it.riskLevel == riskLevel }
    }

    fun printMap(
        map: MutableMap<Position, Region>,
        targetPosition: Position,
        startPosition: Position
    ) {
        val minY = map.keys.minBy { it.y }!!.y
        val maxY = map.keys.maxBy { it.y }!!.y
        val minX = map.keys.minBy { it.x }!!.x
        val maxX = map.keys.maxBy { it.x }!!.x

        println()
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (x == startPosition.x && y == startPosition.y) {
                    print('M')
                } else if (x == targetPosition.x && y == targetPosition.y) {
                    print('T')
                } else {
                    print(map[Position(x, y)]!!.type.code)
                }
            }
            println()
        }
        println()
    }

    fun calculateRiskLevelFor(
        map: MutableMap<Position, Region>,
        startPosition: Position,
        targetPosition: Position
    ): Int {
        return map
            .filter { it.key.x >= startPosition.x && it.key.x <= targetPosition.x }
            .filter { it.key.y >= startPosition.y && it.key.y <= targetPosition.y }
            .map { it.value.type.riskLevel }
            .sum()
    }
}
