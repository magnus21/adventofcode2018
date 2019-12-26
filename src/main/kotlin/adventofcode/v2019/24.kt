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

                if(states.contains(newState)) {
                   val result =  calculateBioDiversity(stateMap, size)
                    println("Answer part 1: $result")
                    break
                }

                states.add(newState)

                printMap(stateMap, size)
            }

        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {


        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun calculateBioDiversity(
        stateMap: MutableMap<Position, Char>,
        size: Pair<Int, Int>
    ): Int {
        var factor = 1
        var result = 0
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                if(stateMap[Position(x,y)] == '#') {
                    result += factor
                }

                factor *=2
            }
        }
        return result
    }

    private fun getNewState(
        tile: MutableMap.MutableEntry<Position, Char>,
        map: MutableMap<Position, Char>
    ): Char {
        val neighbours = getNeighbours(tile.key, map)

        if (tile.value == '#') {
            return if (neighbours.filter { it.second == '#' }.count() != 1) '.' else '#'
        } else {
            val bugCount = neighbours.filter { it.second == '#' }.count()
            return if (bugCount == 1 || bugCount == 2) '#' else '.'
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


    private fun printMap(
        map: MutableMap<Position, Char>,
        size: Pair<Int, Int>
    ) {
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                val tile = map[Position(x, y)]
                print(tile)
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