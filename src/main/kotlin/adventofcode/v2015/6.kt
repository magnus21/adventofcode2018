package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.math.max
import kotlin.system.measureTimeMillis

object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2015, "6.txt")

        val instructions = parseInput(input)

        val time1 = measureTimeMillis {
            val lights = mutableSetOf<Position>()

            instructions.forEach {
                val start = it.positions.first
                val end = it.positions.second
                val action = when (it.type) {
                    "turnon" -> { pos: Position -> lights.add(pos) }
                    "turnoff" -> { pos: Position -> lights.remove(pos) }
                    else -> { pos: Position -> if (lights.contains(pos)) lights.remove(pos) else lights.add(pos) }
                }

                for (x in start.x..end.x) {
                    for (y in start.y..end.y) {
                        action(Position(x, y))
                    }
                }
            }
            println("Part 1: ${lights.size}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            val lights = mutableMapOf<Position, Int>()

            instructions.forEach {
                val start = it.positions.first
                val end = it.positions.second
                val action = when (it.type) {
                    "turnon" -> { pos: Position -> lights[pos] = lights.getOrDefault(pos, 0) + 1 }
                    "turnoff" -> { pos: Position -> lights[pos] = max(0, lights.getOrDefault(pos, 0) - 1) }
                    else -> { pos: Position -> lights[pos] = lights.getOrDefault(pos, 0) + 2 }
                }

                for (x in start.x..end.x) {
                    for (y in start.y..end.y) {
                        action(Position(x, y))
                    }
                }
            }
            println("Part 2: ${lights.map { it.value }.sum()}")
        }
        println("Time: $time2 ms")
    }

    data class Position(val x: Int, val y: Int)

    data class Instruction(val type: String, val positions: Pair<Position, Position>)

    private fun parseInput(input: List<String>): List<Instruction> {
        return input.map {
            val parts = it.split(' ', ',')
            when {
                parts[0] == "turn" -> Instruction(
                    parts[0] + parts[1],
                    Pair(Position(parts[2].toInt(), parts[3].toInt()), Position(parts[5].toInt(), parts[6].toInt()))
                )
                else -> Instruction(
                    parts[0],
                    Pair(Position(parts[1].toInt(), parts[2].toInt()), Position(parts[4].toInt(), parts[5].toInt()))
                )
            }
        }
    }
}
