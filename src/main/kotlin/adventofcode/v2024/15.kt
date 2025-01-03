package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.AdventOfCodeUtil.getBoundaries
import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day15 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2024, "15.txt")

        printResult("part 1") { part1(input) }
        printResult("part 2") { part2(input) }
    }

    private fun part1(input: List<String>): Int {
        val map = parseMap(input)
        val directions = parseDirections(input)

        return runSimulation(map, directions, listOf('O'))
    }

    private fun part2(input: List<String>): Int {
        val inputPart2 = input.takeWhile { it.isNotBlank() }.map { row ->
            row.toCharArray().flatMap { c ->
                when (c) {
                    '#' -> listOf(c, c)
                    'O' -> listOf('[', ']')
                    else -> listOf(c, '.')
                }
            }.joinToString("")
        }
        val map = parseMap(inputPart2)
        val directions = parseDirections(input)

        return runSimulation(map, directions, listOf('[', ']'))
    }

    private fun runSimulation(
        map: Map<Point, Char>,
        directions: List<Point>,
        boxChars: List<Char>
    ): Int {
        var pos = map.filter { it.value == '@' }.keys.first()
        val mutableMap = map.toMutableMap()
        directions.forEach { d ->
            val nextPos = Point(pos.x + d.x, pos.y + d.y)
            val nextValue = mutableMap[nextPos]

            if (nextValue == '.') {
                mutableMap[nextPos] = '@'
                mutableMap[pos] = '.'
                pos = nextPos
            } else if (boxChars.contains(nextValue)) {
                pos =
                    if (boxChars[0] == 'O') tryPush(pos, nextPos, d, mutableMap)
                    else tryBoxPush(pos, d, mutableMap)
            }
            //printMap(mutableMap)
        }
        return mutableMap.filter { it.value == boxChars[0] }.map { it.key.y * 100 + it.key.x }.sum()
    }


    private fun printMap(map: Map<Point, Char>) {
        val boundaries = getBoundaries(map.keys.map { Pair(it.x, it.y) })
        for (y in boundaries.ymin..boundaries.ymax) {
            for (x in boundaries.xmin..boundaries.xmax) {
                print(map[Point(x, y)])
            }
            println()
        }
    }

    private fun tryPush(
        pos: Point, nextPos: Point, d: Point, map: MutableMap<Point, Char>
    ): Point {
        val train = mutableListOf(nextPos)
        var push = false
        while (true) {
            val next = Point(nextPos.x + train.size * d.x, nextPos.y + train.size * d.y)
            if (map[next] == 'O') {
                train.add(next)
            } else {
                if (map[next] == '.') {
                    push = true
                }
                break
            }
        }

        if (push) {
            map[pos] = '.'
            map[Point(pos.x + d.x, pos.y + d.y)] = '@'
            train.forEach { map[Point(it.x + d.x, it.y + d.y)] = 'O' }
            return nextPos
        }

        return pos
    }

    private fun tryBoxPush(
        pos: Point, d: Point, map: MutableMap<Point, Char>
    ): Point {

        val boxesToPush = getBoxesToPush(pos, d, map)
        val canBePushed = boxesToPush.all { it.second }
        if (canBePushed) {
            val boxPositionToMove = boxesToPush.flatMap { it.first }.toSet().map {
                Triple(it, map[it]!!, Point(it.x + d.x, it.y + d.y))
            }
            map[pos] = '.'
            boxPositionToMove.forEach { map[it.first] = '.' }

            val nextPos = Point(pos.x + d.x, pos.y + d.y)
            map[nextPos] = '@'
            boxPositionToMove.forEach { map[it.third] = it.second }

            return nextPos
        }

        return pos
    }

    private fun getBoxesToPush(
        pos: Point,
        d: Point,
        map: MutableMap<Point, Char>,
        blocksToPush: List<Point> = emptyList()
    ): List<Pair<Set<Point>, Boolean>> {

        val nextPos = Point(pos.x + d.x, pos.y + d.y)
        val nextValue = map[nextPos]
        if (nextValue == '#') {
            return listOf(Pair(emptySet(), false))
        } else if (nextValue == '.') {
            return listOf(Pair(blocksToPush.toSet(), true))
        }

        val otherBoxPos = if (d.x != 0) null
        else (if (nextValue == ']') Point(nextPos.x - 1, nextPos.y) else Point(nextPos.x + 1, nextPos.y))

        return setOf(nextPos, otherBoxPos).filterNotNull().flatMap { getBoxesToPush(it, d, map, blocksToPush.plus(it)) }
    }

    private fun parseMap(input: List<String>): Map<Point, Char> {
        return input.takeWhile { it.isNotBlank() }.flatMapIndexed { y, row ->
            row.toCharArray().mapIndexed { x, c -> Pair(Point(x, y), c) }
        }.toMap()
    }

    private fun parseDirections(input: List<String>): List<Point> {
        return input.dropWhile { it.isNotBlank() }.filter { it.isNotBlank() }.flatMap { row ->
            row.toCharArray().map {
                when (it) {
                    '^' -> Point(0, -1)
                    'v' -> Point(0, 1)
                    '>' -> Point(1, 0)
                    else -> Point(-1, 0)
                }
            }
        }
    }
}
