package adventofcode.v2020

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "17.txt")
        val (size, map) = parseMap(input)

        printMap(map, size)

        val time1 = measureTimeMillis {
            var newState = map.toMutableMap()
            var fieldSize = size
            var count = 0
            while (count++ < 6) {
                newState = getState(newState, fieldSize)

                fieldSize = getFieldSize(newState)

                println("\n======== After $count cycles:========\n")
                printMap(newState, fieldSize)
            }

            val answer = newState.values.filter { it == '#' }.count()
            println("Answer part 2: $answer")
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            var newState = map.toMutableMap()
            var fieldSize = size
            var count = 0
            while (count++ < 6) {
                newState = getState4D(newState, fieldSize)

                fieldSize = getFieldSize(newState)

                println("\n======== After $count cycles:========\n")
                printMap4D(newState, fieldSize)
            }

            val answer = newState.values.filter { it == '#' }.count()
            println("Answer part 2: $answer")

        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun getState(map: MutableMap<Position, Char>, size: List<Pair<Int, Int>>): MutableMap<Position, Char> {
        val newState = mutableMapOf<Position, Char>()

        for (z in size[2].first - 1..size[2].second + 1) {
            for (y in size[1].first - 1..size[1].second + 1) {
                for (x in size[0].first - 1..size[0].second + 1) {
                    val pos = Position(x, y, z)
                    newState[pos] = getNextStateForPosition(pos, map)
                }
            }
        }
        return newState
    }

    private fun getState4D(map: MutableMap<Position, Char>, size: List<Pair<Int, Int>>): MutableMap<Position, Char> {
        val newState = mutableMapOf<Position, Char>()

        for (w in size[3].first - 1..size[3].second + 1) {
            for (z in size[2].first - 1..size[2].second + 1) {
                for (y in size[1].first - 1..size[1].second + 1) {
                    for (x in size[0].first - 1..size[0].second + 1) {
                        val pos = Position(x, y, z, w)
                        newState[pos] = getNextStateFor4DPosition(pos, map)
                    }
                }
            }
        }
        return newState
    }

    private fun getNextStateForPosition(pos: Position, map: MutableMap<Position, Char>): Char {
        val neighbours = AdventOfCodeUtil.getNeighboursXd(listOf(pos.x, pos.y, pos.z))

        val tile = map.getOrDefault(pos, '.')
        val activeNeighbours =
            neighbours
                .map { map.getOrDefault(Position(it[0], it[1], it[2]), '.') }
                .filter { it == '#' }
                .count()
        return if (tile == '#') {
            if (activeNeighbours == 2 || activeNeighbours == 3) '#' else '.'
        } else {
            if (activeNeighbours == 3) '#' else '.'
        }
    }

    private fun getNextStateFor4DPosition(pos: Position, map: MutableMap<Position, Char>): Char {
        val neighbours = AdventOfCodeUtil.getNeighboursXd(listOf(pos.x, pos.y, pos.z, pos.w))

        val tile = map.getOrDefault(pos, '.')
        val activeNeighbours =
            neighbours
                .map { map.getOrDefault(Position(it[0], it[1], it[2], it[3]), '.') }
                .filter { it == '#' }
                .count()
        return if (tile == '#') {
            if (activeNeighbours == 2 || activeNeighbours == 3) '#' else '.'
        } else {
            if (activeNeighbours == 3) '#' else '.'
        }
    }

    private fun parseMap(input: List<String>): Pair<List<Pair<Int, Int>>, MutableMap<Position, Char>> {

        val size = listOf(Pair(0, input.size - 1), Pair(0, input.size - 1), Pair(0, 0), Pair(0, 0))
        val map = mutableMapOf<Position, Char>()

        for (y in size[1].first..size[1].second) {
            val row = input[y]
            for (x in size[0].first..size[0].second) {
                map[Position(x, y, 0, 0)] = row[x]
            }
        }

        return Pair(size, map)
    }

    private fun getFieldSize(input: MutableMap<Position, Char>): List<Pair<Int, Int>> {

        val x = input.keys.map { it.x }.toSet().sorted()
        val y = input.keys.map { it.y }.toSet().sorted()
        val z = input.keys.map { it.z }.toSet().sorted()
        val w = input.keys.map { it.w }.toSet().sorted()

        return listOf(
            Pair(x.first(), x.last()),
            Pair(y.first(), y.last()),
            Pair(z.first(), z.last()),
            Pair(w.first(), w.last())
        )
    }


    private fun printMap(map: MutableMap<Position, Char>, size: List<Pair<Int, Int>>) {
        for (z in size[2].first..size[2].second) {
            println("== z: $z ==")
            for (y in size[1].first..size[1].second) {
                for (x in size[0].first..size[0].second) {
                    val tile = map[Position(x, y, z)]
                    print(tile ?: '.')
                }
                println()
            }
        }
        println()
    }

    private fun printMap4D(map: MutableMap<Position, Char>, size: List<Pair<Int, Int>>) {
        for (w in size[3].first..size[3].second) {
            for (z in size[2].first..size[2].second) {
                println("== z: $z, w: $w ==")
                for (y in size[1].first..size[1].second) {
                    for (x in size[0].first..size[0].second) {
                        val tile = map[Position(x, y, z)]
                        print(tile ?: '.')
                    }
                    println()
                }
            }
            println()
        }
        println()
    }

    private data class Position(val x: Int, val y: Int, val z: Int, val w: Int = 0) {
        override fun toString(): String {
            return "[$x, $y, $z, $w]"
        }
    }
}