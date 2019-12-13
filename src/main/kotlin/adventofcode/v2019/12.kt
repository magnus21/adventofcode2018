package adventofcode.v2019

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.AdventOfCodeUtil.leastCommonMultiple
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "12.txt")

        // Part 1
        val time1 = measureTimeMillis {
            val moons = parseInput(input)
            val moonPairs = AdventOfCodeUtil.generatePairs(moons)
            //moonPairs.forEach(::println)

            for (i in 1..1000) {
                moonPairs.forEach(this::applyGravity)
                moons.forEach(this::applyVelocity)
                //printState(moons, i)
            }

            println(getTotalEnergy(moons))
        }
        println("Time part 1: ($time1 milliseconds)")

        // Part 2
        val time2 = measureTimeMillis {

            val moons = parseInput(input)
            val startMoons = moons.toString()
            val moonPairs = AdventOfCodeUtil.generatePairs(moons)
            //moonPairs.forEach(::println)

            var i = 1L
            val startMoonX = moons.map { it.position.x }
            val startMoonY = moons.map { it.position.y }
            val startMoonZ = moons.map { it.position.z }

            val periods = mutableListOf(0L, 0L, 0L)
            while (true) {
                moonPairs.forEach(this::applyGravity)
                moons.forEach(this::applyVelocity)

                val moonX = moons.map { it.position.x }
                val moonY = moons.map { it.position.y }
                val moonZ = moons.map { it.position.z }

                if (periods[0] == 0L && moonX == startMoonX) {
                    printState(moons, i.toInt())
                    periods[0] = i + 1 // Don't ask....
                }
                if (periods[1] == 0L && moonY == startMoonY) {
                    printState(moons, i.toInt())
                    periods[1] = i + 1
                }
                if (periods[2] == 0L && moonZ == startMoonZ) {
                    printState(moons, i.toInt())
                    periods[2] = i + 1
                }

                if (periods.none { it == 0L }) {
                    break
                }
                i++
            }
            val result = periods.reduce(::leastCommonMultiple)
            println("Result: $result")
        }
    }

    private fun printState(moons: List<Moon>, i: Int) {
        println("After $i steps: ${moons.joinToString("")}")
    }

    private fun getTotalEnergy(moons: List<Moon>): Int {
        return moons.map { getEnergy(it.position) * getEnergy(it.velocity) }.sum()
    }

    private fun getEnergy(coordinate: Coordinate) =
        Math.abs(coordinate.x) + Math.abs(coordinate.y) + Math.abs(coordinate.z)

    private fun applyVelocity(moon: Moon) {
        moon.position.x += moon.velocity.x
        moon.position.y += moon.velocity.y
        moon.position.z += moon.velocity.z
    }

    private fun applyGravity(moonPair: Pair<Moon, Moon>) {
        applyVelocityChange(moonPair, { c: Coordinate -> c.x }, { c: Coordinate, value: Int -> c.x += value })
        applyVelocityChange(moonPair, { c: Coordinate -> c.y }, { c: Coordinate, value: Int -> c.y += value })
        applyVelocityChange(moonPair, { c: Coordinate -> c.z }, { c: Coordinate, value: Int -> c.z += value })
    }

    private fun applyVelocityChange(
        moonPair: Pair<Moon, Moon>,
        getter: (Coordinate) -> Int,
        setter: (Coordinate, Int) -> Unit
    ) {
        val firstPosition = moonPair.first.position
        val secondPosition = moonPair.second.position

        val velocityChange =
            when {
                getter(firstPosition) < getter(secondPosition) -> Pair(1, -1)
                getter(firstPosition) > getter(secondPosition) -> Pair(-1, 1)
                else -> Pair(0, 0)
            }

        setter(moonPair.first.velocity, velocityChange.first)
        setter(moonPair.second.velocity, velocityChange.second)
    }

    private fun parseInput(input: List<String>): List<Moon> {
        return input.map { row ->
            val coordinates =
                row.replace("<", "").replace(">", "").split(",")
                    .map(String::trim).map { it.split("=")[1].toInt() }
            Moon(Coordinate(coordinates[0], coordinates[1], coordinates[2]), Coordinate(0, 0, 0))
        }
    }

    private data class Coordinate(var x: Int, var y: Int, var z: Int) {
        override fun toString(): String {
            return "[$x, $y, $z]"
        }
    }

    private data class Moon(val position: Coordinate, val velocity: Coordinate) {
        override fun toString(): String {
            return "$position,$velocity"
        }

    }
}