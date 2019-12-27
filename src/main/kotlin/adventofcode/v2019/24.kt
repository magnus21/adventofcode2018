package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.Day24.Direction.*
import kotlin.system.measureTimeMillis

object Day24 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "24.txt")
        val (size, map) = parseMap(input)

        printMap(map, size)

        val time1 = measureTimeMillis {
            val states = mutableSetOf<String>()

            var stateMap = map.toMutableMap()
            states.add(getState(stateMap, size))

            while (true) {
                stateMap = stateMap.entries.map { Pair(it.key, getNewState(it, stateMap)) }.toMap().toMutableMap()
                val newState = getState(stateMap, size)

                if (states.contains(newState)) {
                    val result = calculateBioDiversity(stateMap, size)
                    println("Answer part 1: $result")
                    break
                }

                states.add(newState)

                printMap(stateMap, size)
            }

        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            var levels: MutableMap<Int, MutableMap<Position, Char>> = mutableMapOf()
            levels[0] = map.toMutableMap()

            for (i in 1..200) {
                val keys = levels.keys
                val limits = Pair(keys.min()!!, keys.max()!!)
                levels[limits.first - 1] = createEmptyLevel()
                levels[limits.second + 1] = createEmptyLevel()

                levels = levels.entries.map { Pair(it.key, getNewStateForLevel(it.key, it.value, levels)) }.toMap()
                    .toMutableMap()
            }

            //printLevels(levels,size)

            val bugCount = levels
                .map { level -> level.value.entries.filter { it.value == '#' }.count() }.sum()

            println("Answer part 2: $bugCount")

        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun createEmptyLevel(): MutableMap<Position, Char> {
        return parseMap((1..5).map { "....." }).second
    }

    private fun calculateBioDiversity(
        stateMap: MutableMap<Position, Char>,
        size: Pair<Int, Int>
    ): Int {
        var factor = 1
        var result = 0
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                if (stateMap[Position(x, y)] == '#') {
                    result += factor
                }

                factor *= 2
            }
        }
        return result
    }

    private fun getNewState(
        tile: MutableMap.MutableEntry<Position, Char>,
        map: MutableMap<Position, Char>
    ): Char {
        val neighbours = getNeighbours(tile.key, map)

        return if (tile.value == '#') {
            if (neighbours.filter { it.second == '#' }.count() != 1) '.' else '#'
        } else {
            val bugCount = neighbours.filter { it.second == '#' }.count()
            if (bugCount == 1 || bugCount == 2) '#' else '.'
        }
    }

    private fun getNewStateForLevel(
        levelKey: Int,
        level: MutableMap<Position, Char>,
        levels: MutableMap<Int, MutableMap<Position, Char>>
    ): MutableMap<Position, Char> {
        return level.entries.filter { it.key.x != 2 || it.key.y != 2 }
            .map { Pair(it.key, getNewStateForPosition(it, levelKey, level, levels)) }.toMap().toMutableMap()
    }

    private fun getNewStateForPosition(
        tile: MutableMap.MutableEntry<Position, Char>,
        levelKey: Int,
        level: MutableMap<Position, Char>,
        levels: MutableMap<Int, MutableMap<Position, Char>>
    ): Char {
        val neighbours = getNeighboursRecursively(tile.key, levelKey, level, levels)

        return if (tile.value == '#') {
            if (neighbours.filter { it == '#' }.count() != 1) '.' else '#'
        } else {
            val bugCount = neighbours.filter { it == '#' }.count()
            if (bugCount == 1 || bugCount == 2) '#' else '.'
        }
    }

    private fun getNeighboursRecursively(
        position: Position,
        levelKey: Int,
        level: MutableMap<Position, Char>,
        levels: MutableMap<Int, MutableMap<Position, Char>>
    ): List<Char> {

        return values()
            .map { getPosition(position, it) }
            .flatMap { pos ->
                val list = mutableListOf<Char>()
                    if (pos.x == 2 && pos.y == 2) {
                        if (levels[levelKey + 1] == null) {
                            list.add('.')
                        } else {
                            list.addAll(when {
                                position.x == 1 -> levels[levelKey + 1]!!.entries.filter { it.key.x == 0 }.map { it.value }
                                position.x == 3 -> levels[levelKey + 1]!!.entries.filter { it.key.x == 4 }.map { it.value }
                                position.y == 1 -> levels[levelKey + 1]!!.entries.filter { it.key.y == 0 }.map { it.value }
                                else -> levels[levelKey + 1]!!.entries.filter { it.key.y == 4 }.map { it.value }
                            })
                        }
                    } else if (pos.x == -1) {
                        if (levels[levelKey - 1] == null) {
                            list.add('.')
                        } else {
                            list.add(levels[levelKey - 1]!![Position(1, 2)]!!)
                        }
                    } else if (pos.x == 5) {
                        if (levels[levelKey - 1] == null) {
                            list.add('.')
                        } else {
                            list.add(levels[levelKey - 1]!![Position(3, 2)]!!)
                        }
                    } else if (pos.y == -1) {
                        if (levels[levelKey - 1] == null) {
                            list.add('.')
                        } else {
                            list.add(levels[levelKey - 1]!![Position(2, 1)]!!)
                        }
                    } else if (pos.y == 5) {
                        if (levels[levelKey - 1] == null) {
                            list.add('.')
                        } else {
                            list.add(levels[levelKey - 1]!![Position(2, 3)]!!)
                        }
                    } else {
                        list.add(level[pos]!!)
                    }
                list
            }
    }

    private fun getNeighbours(position: Position, map: MutableMap<Position, Char>): List<Pair<Position, Char>> {
        return values()
            .map { getPosition(position, it) }
            .filter { map[it] != null }
            .map { Pair(it, map[it]!!) }
    }

    private fun getPosition(
        startPosition: Position,
        direction: Direction
    ): Position {
        return when (direction) {
            NORTH -> Position(startPosition.x, startPosition.y - 1)
            SOUTH -> Position(startPosition.x, startPosition.y + 1)
            WEST -> Position(startPosition.x - 1, startPosition.y)
            else -> Position(startPosition.x + 1, startPosition.y)
        }
    }

    private fun getState(
        map: MutableMap<Position, Char>,
        size: Pair<Int, Int>
    ): String {
        var state = ""
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                state += map[Position(x, y)]
            }
        }
        return state
    }

    private fun parseMap(input: List<String>): Pair<Pair<Int, Int>, MutableMap<Position, Char>> {

        val fieldSize = Pair(input.map { it.length }.max()!!, input.size)
        val map = mutableMapOf<Position, Char>()

        for (y in 0 until fieldSize.second) {
            val row = input[y]
            for (x in 0 until fieldSize.first) {
                map[Position(x, y)] = row[x]
            }
        }

        return Pair(fieldSize, map)
    }

    private fun printLevels(
        levels: MutableMap<Int, MutableMap<Position, Char>>,
        size: Pair<Int, Int>
    ) {

        levels.toSortedMap().entries
            .filter { it.value.filter { tile -> tile.value == '#' }.count() > 0 }
            .forEach {
                println("Depth ${it.key}")
                printMap(it.value, size)
            }
    }

    private fun printMap(
        map: MutableMap<Position, Char>,
        size: Pair<Int, Int>
    ) {
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                val tile = map[Position(x, y)]
                print(if(tile == null) '?' else tile)
            }
            println()
        }
        println()
    }

    private data class Position(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }

    enum class Direction(val code: Int) {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4)
    }
}