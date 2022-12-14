package adventofcode.util

import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

object AdventOfCodeUtil {

    @ExperimentalTime
    fun <T> printResult(message: String, block: () -> T) {
        val (result, duration) = measureTimedValue {
            block()
        }
        println("$message: $result ($duration)")
    }

    fun <T> generatePermutations(
        list: List<T>,
        length: Int = list.size,
        result: MutableList<List<T>> = mutableListOf(),
        permutation: List<T> = listOf()
    ): List<List<T>> {
        for (i in list.indices) {
            if (permutation.size == length - 1) {
                result.add(permutation.plusElement(list[i]))
                break
            }

            val listCopy = list.toMutableList()
            listCopy.removeAt(i)
            generatePermutations(listCopy, length, result, permutation.plusElement(list[i]))
        }

        return result
    }

    fun <T> generatePairs(list: List<T>): MutableList<Pair<T, T>> {
        val result = mutableListOf<Pair<T, T>>()
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                result.add(Pair(list[i], list[j]))
            }
        }

        return result
    }

    fun <T> combinations(
        set: Set<T>,
        size: Int,
        accumulated: Set<T>,
        combinations: MutableList<Set<T>> = mutableListOf()
    ): List<Set<T>> {
        // 1. stop
        if (set.size < size) {
            return emptyList()
        }
        // 2. add each element in e to accumulated
        when {
            size == 1 -> {
                combinations.addAll(set.map { accumulated.toMutableSet().plus(it) })
            }
            // 3. add all elements in e to accumulated
            set.size == size -> {
                combinations.addAll(listOf(accumulated.plus(set)))
            }
            // 4. for each element, call combination
            else -> {
                set.forEach { combinations(set.minus(it), size - 1, accumulated.plus(it), combinations) }
            }
        }

        return combinations
    }

    fun getNeighboursXd(pos: List<Int>): Set<List<Int>> {

        val level = pos.size
        val result = mutableSetOf<List<Int>>()

        return getNeighbourCoords(pos, level, result, mutableListOf())
            .filter { !it.all { n -> n == 0 } }
            .map { it.mapIndexed { i, n -> pos[i] + n } }
            .toSet()
    }

    private fun getNeighbourCoords(
        pos: List<Int>,
        level: Int,
        result: MutableSet<List<Int>>,
        neighbour: List<Int>
    ): Set<List<Int>> {

        if (level == 1) {
            setOf(-1, 0, 1).forEach {
                result.add(neighbour.plus(it))
            }
        } else {
            setOf(-1, 0, 1).forEach {
                getNeighbourCoords(pos, level - 1, result, neighbour.plus(it))
            }
        }

        return result
    }

    fun getNeighbours3d(x: Int, y: Int, z: Int): Set<Triple<Int, Int, Int>> {
        return setOf(-1, 0, 1).flatMap { zz ->
            setOf(-1, 0, 1).flatMap { yy ->
                setOf(-1, 0, 1).map { xx ->
                    if (zz == 0 && yy == 0 && xx == 0) null else Triple(x + xx, y + yy, z + zz)
                }
            }
        }.filterNotNull().toSet()
    }

    fun getNeighbours2d(pos: Pair<Int, Int>): Set<Pair<Int, Int>> {
        return getNeighbours2d(pos.first, pos.second)
    }

    fun getNeighbours2d(x: Int, y: Int): Set<Pair<Int, Int>> {
        return setOf(-1, 0, 1).flatMap { yy ->
            setOf(-1, 0, 1).map { xx ->
                if (yy == 0 && xx == 0) null else Pair(x + xx, y + yy)
            }
        }.filterNotNull().toSet()
    }

    fun getPerpendicularNeighbours2d(x: Int, y: Int): Set<Pair<Int, Int>> {
        return setOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)).map { pos ->
            Pair(x + pos.first, y + pos.second)
        }.toSet()
    }

    fun greatestCommonDivisor(a: Int, b: Int): Int {
        return if (b == 0) a else greatestCommonDivisor(b, a % b)
    }

    fun greatestCommonDivisor(a: Long, b: Long): Long {
        return if (b == 0L) a else greatestCommonDivisor(b, a % b)
    }

    fun leastCommonMultiple(a: Long, b: Long): Long {
        return a * b / greatestCommonDivisor(a, b)
    }

    fun <T, U> reduceOneToManyMatches(possibleMatches: List<Pair<T, List<U>>>): Map<T, U> {
        val pickedMatch = mutableSetOf<U>()
        return possibleMatches
            .sortedBy { it.second.size }.associate {
                val chosen = it.second.first { p -> !pickedMatch.contains(p) }
                pickedMatch.add(chosen)
                Pair(it.first, chosen)
            }
    }

    class Boundaries(var xmin: Int, var ymin: Int, var xmax: Int, var ymax: Int)

    fun getBoundaries(points: Iterable<Pair<Int, Int>>): Boundaries {
        return points.fold(Boundaries(Int.MAX_VALUE, Int.MAX_VALUE, -1, -1)) { bounds, point ->
            if (point.first < bounds.xmin)
                bounds.xmin = point.first
            if (point.second < bounds.ymin)
                bounds.ymin = point.second
            if (point.first > bounds.xmax)
                bounds.xmax = point.first
            if (point.second > bounds.ymax)
                bounds.ymax = point.second

            bounds
        }
    }

    fun isWithInBoundaries(boundaries: Boundaries, point: Point): Boolean {
        return point.x >= boundaries.xmin && point.x <= boundaries.xmax &&
                point.y >= boundaries.ymin && point.y <= boundaries.ymax
    }

    fun printPoints(points: MutableSet<Pair<Int, Int>>, printBlanks: Boolean = false, blankChar: Char = '.') {
        val boundaries = getBoundaries(points)
        for (y in boundaries.ymin..boundaries.ymax) {
            for (x in boundaries.xmin..boundaries.xmax) {
                print(if (points.any { p -> p.first == x && p.second == y }) "#" else (if (printBlanks) blankChar else " "))
            }
            println()
        }
    }

    fun printPointsMap(points: Map<Pair<Int, Int>, Char>, printBlanks: Boolean = true, blankChar: Char = '.') {
        val boundaries = getBoundaries(points.keys.map { Pair(it.first, it.second) }.toMutableSet())
        for (y in boundaries.ymin..boundaries.ymax) {
            for (x in boundaries.xmin..boundaries.xmax) {
                print(
                    if (points.keys.any { p -> p.first == x && p.second == y }) points[Pair(
                        x,
                        y
                    )] else (if (printBlanks) blankChar else " ")
                )
            }
            println()
        }
    }

    data class Point(val x: Int, val y: Int, val z: Int = 0)

    fun getAbsDistance(from: Point, to: Point): Point {
        return Point(abs(to.x - from.x), abs(to.y - from.y), abs(to.z - from.z));
    }

    fun manhattanDistance(from: Point, to: Point): Int {
        return abs(from.x - to.x) +
                abs(from.y - to.y) +
                abs(from.z - to.z);
    }


}