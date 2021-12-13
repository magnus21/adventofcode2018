package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {
        val (dots, folds) = parseInput(FileParser.getFileRows(2021, "13.txt"))

        val time1 = measureTimeMillis {
            var startDots = dots.toMutableSet()
            folds.subList(0, 1).forEach { fold -> startDots = foldDots(fold, startDots) }

            println("answer part 1: ${startDots.count()}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            var startDots = dots.toMutableSet()
            folds.forEach { fold -> startDots = foldDots(fold, startDots) }

            AdventOfCodeUtil.printPoints(startDots)
        }
        println("Time: $time2 ms")
    }

    private fun foldDots(fold: Pair<String, Int>, startDots: MutableSet<Pair<Int, Int>>): MutableSet<Pair<Int, Int>> {
        return when (fold.first) {
            "x" -> {
                val xFold = fold.second
                startDots.map { dot ->
                    val diff = dot.first - xFold
                    if (diff > 0) Pair(xFold - diff, dot.second) else dot
                }
            }
            else -> {
                val yFold = fold.second
                startDots.map { dot ->
                    val diff = dot.second - yFold
                    if (diff > 0) Pair(dot.first, yFold - diff) else dot
                }
            }
        }.toMutableSet()
    }

    private fun parseInput(rows: List<String>): Pair<MutableSet<Pair<Int, Int>>, MutableList<Pair<String, Int>>> {
        var foldInstructions = false
        val dots = mutableSetOf<Pair<Int, Int>>()
        val folds = mutableListOf<Pair<String, Int>>()
        for (row in rows) {
            if (row.isBlank()) {
                foldInstructions = true
                continue
            }
            when {
                !foldInstructions -> {
                    val coordinates = row.trim().split(",").map { it.toInt() }
                    dots.add(Pair(coordinates[0], coordinates[1]))
                }
                else -> {
                    val coordinates = row.trim().split(" ", "=")
                    folds.add(Pair(coordinates[2], coordinates[3].toInt()))
                }
            }
        }
        return Pair(dots, folds)
    }
}