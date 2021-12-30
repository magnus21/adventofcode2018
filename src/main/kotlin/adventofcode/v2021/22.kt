package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil.Point
import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

object Day22 {

    @JvmStatic
    fun main(args: Array<String>) {

        val time1 = measureTimeMillis {
            val cubiods = parseInput(FileParser.getFileRows(2021, "22.txt"))

            val cubes = mutableSetOf<Point>()
            cubiods.forEach { cubiod ->
                cubiod.x.filter { it >= -50 && it <= 50 }.forEach { x ->
                    cubiod.y.filter { it >= -50 && it <= 50 }.forEach { y ->
                        cubiod.z.filter { it >= -50 && it <= 50 }.forEach { z ->
                            val cube = Point(x, y, z)
                            if (cubiod.on) {
                                cubes.add(cube)
                            } else {
                                cubes.remove(cube)
                            }
                        }
                    }
                }
            }

            println("answer part 1: ${cubes.size}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val cubiods = parseInput(FileParser.getFileRows(2021, "22.txt"))
            val currentCubiods = getFinalCubiods(cubiods)
            val sums = getCubeSum(currentCubiods)
            val answer = sums.sum()

            println("answer part 2: $answer")
        }
        println("Time: $time2 ms")
    }

    private fun getFinalCubiods(cubiods: List<Cubiod>): Set<Cubiod> {
        var currentCubiods = setOf<Cubiod>()

        cubiods.forEachIndexed { i, cubiod ->
            val splitted = currentCubiods.flatMap { splitAndRemoveIfOverlap(it, cubiod) }.toSet()
            currentCubiods = if (cubiod.on) splitted.plus(cubiod) else splitted
        }
        return currentCubiods
    }

    private fun getCubeSum(currentCubiods: Iterable<Cubiod>) =
        currentCubiods.map {
            val sign = if (it.on) 1 else -1
            sign * (
                    (abs(it.x.last - it.x.first) + 1).toLong() *
                            (abs(it.y.last - it.y.first) + 1).toLong() *
                            (abs(it.z.last - it.z.first) + 1).toLong()
                    )
        }

    private fun splitAndRemoveIfOverlap(splitting: Cubiod, b: Cubiod): List<Cubiod> {
        val overlap = overLapAsCubiod(splitting, b)

        if (overlap != null) {
            return setOf(
                splitRange(splitting.x, overlap.x).map { Cubiod(true, it, splitting.y, splitting.z) },
                splitRange(splitting.y, overlap.y).map { Cubiod(true, overlap.x, it, splitting.z) },
                splitRange(splitting.z, overlap.z).map { Cubiod(true, overlap.x, overlap.y, it) }
            ).flatten()
        }
        return listOf(splitting)
    }

    private fun splitRange(splitting: IntRange, overlap: IntRange): MutableList<IntRange> {
        val ranges = mutableListOf<IntRange>()
        if (overlap.first > splitting.first) {
            ranges.add(splitting.first until overlap.first)
        }
        if (overlap.last < splitting.last) {
            ranges.add(overlap.last + 1..splitting.last)
        }

        return ranges
    }

    private fun cubiodsOverLaps(splittedCubiod: Cubiod, cubiod: Cubiod): Boolean {
        return rangesOverlap(splittedCubiod.x, cubiod.x) &&
                rangesOverlap(splittedCubiod.y, cubiod.y) &&
                rangesOverlap(splittedCubiod.z, cubiod.z)
    }

    private fun overLapAsCubiod(a: Cubiod, b: Cubiod, mode: Boolean = true): Cubiod? {
        return if (cubiodsOverLaps(a, b)) Cubiod(
            mode,
            rangeOverlap(a.x, b.x),
            rangeOverlap(a.y, b.y),
            rangeOverlap(a.z, b.z)
        ) else null
    }

    private fun rangesOverlap(a: IntRange, b: IntRange) = a.last >= b.first && a.first <= b.last
    private fun rangeOverlap(a: IntRange, b: IntRange) = max(a.first, b.first)..min(a.last, b.last)

    private fun parseInput(rows: List<String>): List<Cubiod> {
        val regex = """(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)""".toRegex()
        return rows.mapIndexed { i, row ->
            val matches = regex.find(row)!!.destructured.toList()
            val ranges = matches.drop(1).map { it.toInt() }
            val on = matches[0] == "on"
            Cubiod(on, ranges[0]..ranges[1], ranges[2]..ranges[3], ranges[4]..ranges[5], i)
        }
    }

    data class Cubiod(val on: Boolean, val x: IntRange, val y: IntRange, val z: IntRange, val index: Int = -1)
}