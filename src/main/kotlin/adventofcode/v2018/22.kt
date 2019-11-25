package adventofcode.v2018

import adventofcode.v2018.RegionType.*
import adventofcode.v2018.Tool.*
import adventofcode.util.Queue

fun main(args: Array<String>) {

    val depth = 11394
    val targetPosition = Position(7, 701)

    // Test data
    //val depth = 510
    //val targetPosition = Position(10, 10)

    val startPosition = Position(0, 0)

    val map = mutableMapOf<Position, Region>()

    Day22.createMap(depth, targetPosition, startPosition, map)

    Day22.printMap(map, targetPosition, startPosition)

    val riskLevel = Day22.calculateRiskLevelFor(map, startPosition, targetPosition)

    println("Risk level: $riskLevel")

    val pathQueue = Queue<PathPosition>()
    val reachedPositionsPerTool = mutableMapOf<Pair<Position, Tool>, Int>()

    pathQueue.enqueue(PathPosition(mutableListOf(startPosition), 0, TORCH))
    reachedPositionsPerTool.put(Pair(startPosition, TORCH), 0)

    val targetPaths = mutableListOf<PathPosition>()
    while (pathQueue.isNotEmpty()) {
        pathQueue.sortQueue(compareBy { it.time })
        Day22.explorePaths(pathQueue, map, reachedPositionsPerTool, targetPosition, targetPaths)
    }

    targetPaths
        .sortedBy { it.time }
        .take(1)
        .forEach {
            println(it.time)
            println()
            println()
        }
}

enum class RegionType(val code: Char, val riskLevel: Int) {
    ROCKY('.', 0),
    WET('=', 1),
    NARROW('|', 2)
}

enum class Tool {
    TORCH,
    CLIMBING_GEAR,
    NEITHER
}

data class Region(val position: Position, val geologicalIndex: Int, val erosionLevel: Int, val type: RegionType)

data class PathPosition(val path: MutableList<Position>, var time: Int, var tool: Tool)

object Day22 {

    private fun getPossibleToolsFor(region: Region): Set<Tool> {
        return when (region.type) {
            ROCKY -> setOf(TORCH, CLIMBING_GEAR)
            WET -> setOf(CLIMBING_GEAR, NEITHER)
            NARROW -> setOf(TORCH, NEITHER)
        }
    }

    fun explorePaths(
        pathQueue: Queue<PathPosition>,
        map: MutableMap<Position, Region>,
        reachedPositions: MutableMap<Pair<Position, Tool>, Int>,
        targetPosition: Position,
        targetPaths: MutableList<PathPosition>
    ) {
        val pathPosition = pathQueue.dequeue()!!
        val currentBestTime = targetPaths.map { it.time }.min()
        val currentTool = pathPosition.tool
        val currentPosition = pathPosition.path.last()
        val currentRegion = map.get(currentPosition)!!
        val otherPossibleTool = getPossibleToolsFor(currentRegion).first { tool -> tool != currentTool }

        if (currentPosition == targetPosition && currentTool == TORCH) {
            targetPaths.add(PathPosition(pathPosition.path, pathPosition.time, TORCH))
        } else if (pathPosition.time + 7 < reachedPositions.getOrDefault(Pair(currentPosition, otherPossibleTool), Int.MAX_VALUE)) {
            val pathList = mutableListOf<Position>()
            pathList.addAll(pathPosition.path)

            val newPathPosition = PathPosition(pathList, pathPosition.time + 7, otherPossibleTool)
            pathQueue.enqueue(newPathPosition)

            reachedPositions.put(Pair(currentPosition, otherPossibleTool), pathPosition.time + 7)
        }

        listOf(Pair(0, 1), Pair(1, 0), Pair(-1, 0), Pair(0, -1))
            .map { direction ->
                Position(
                    pathPosition.path.last().x + direction.first,
                    pathPosition.path.last().y + direction.second
                )
            }
            .filter { it.x >= 0 && it.y >= 0 }
            .filter { !pathPosition.path.contains(it) }
            .filter { currentBestTime == null || pathPosition.time + 1 < currentBestTime }
            .forEach {
                val region = map.get(it)
                //{ key -> calculateRegion(it, startPosition, targetPosition, map, depth) }
                if (region != null && getPossibleToolsFor(region).any { it == currentTool }) {
                    val time = pathPosition.time + 1

                    if (time < reachedPositions.getOrDefault(Pair(it, currentTool), Int.MAX_VALUE)) {
                        // Shortest path so far
                        val pathList = mutableListOf<Position>()
                        pathList.addAll(pathPosition.path)
                        pathList.add(it)

                        val newPathPosition = PathPosition(pathList, time, currentTool)
                        pathQueue.enqueue(newPathPosition)

                        reachedPositions.put(Pair(it, currentTool), time)
                    }
                }
            }
    }

    fun createMap(depth: Int, targetPosition: Position, startPosition: Position, map: MutableMap<Position, Region>) {
        // Add some reasonable padding.. no time for fancy stuff.
        for (y in startPosition.y..(targetPosition.y + 20)) {
            for (x in startPosition.x..(targetPosition.x + 35)) {
                calculateRegion(
                    Position(x, y),
                    startPosition,
                    targetPosition,
                    map,
                    depth
                )
            }
        }
    }

    private fun calculateRegion(
        position: Position,
        startPosition: Position,
        targetPosition: Position,
        map: MutableMap<Position, Region>,
        depth: Int
    ): Region {
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

        val region = Region(position, geologicalIndex, erosionLevel, type)

        map.put(position, region)

        return region
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
            print(y.toString().padEnd(4, ' '))
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
