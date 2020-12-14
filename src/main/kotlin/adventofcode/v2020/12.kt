package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "12.txt")
        val instructions = Day12.parseInstructions(input)


        val time1 = measureTimeMillis {
            val endPosition = instructions.fold(Ferry(0, 0, Direction.E)) { ferry, ins ->
                when (ins.action) {
                    Action.F -> propelFerry(ins, ferry)
                    Action.R, Action.L -> turnFerry(ins, ferry)
                    else -> moveFerry(ins, ferry)
                }
            }
            println("Answer part 1: $endPosition : ${abs(endPosition.x) + abs(endPosition.y)}")
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            val endPosition = instructions.fold(Ferry(0, 0, Direction.E)) { ferry, ins ->
                when (ins.action) {
                    Action.F -> propelFerry2(ins, ferry)
                    Action.R, Action.L -> rotateWayPoint(ins, ferry)
                    else -> moveWayPoint(ins, ferry)
                }
            }

            println("Answer part 2: $endPosition : ${abs(endPosition.x) + abs(endPosition.y)}")
        }
        println("Time part 2: ($time2 milliseconds)")

    }

    private fun moveWayPoint(ins: Instruction, f: Ferry): Ferry {
        val wayPoint = when (ins.action) {
            Action.E -> WayPoint(f.wayPoint.x + ins.value, f.wayPoint.y)
            Action.S -> WayPoint(f.wayPoint.x, f.wayPoint.y - ins.value)
            Action.W -> WayPoint(f.wayPoint.x - ins.value, f.wayPoint.y)
            else -> WayPoint(f.wayPoint.x, f.wayPoint.y + ins.value)
        }
        return Ferry(f.x, f.y, f.direction, wayPoint)
    }

    private fun rotateWayPoint(ins: Instruction, f: Ferry): Ferry {
        val wayPoint = if (ins.action == Action.R) f.wayPoint.rotateCW(ins.value)
        else f.wayPoint.rotateCCW(ins.value)

        return Ferry(f.x, f.y, f.direction, wayPoint)
    }


    private fun propelFerry2(ins: Instruction, f: Ferry): Ferry {
        return Ferry(f.x + f.wayPoint.x * ins.value, f.y + f.wayPoint.y * ins.value, f.direction, f.wayPoint)
    }


    private fun moveFerry(ins: Instruction, f: Ferry): Ferry {
        return when (ins.action) {
            Action.E -> Ferry(f.x + ins.value, f.y, f.direction)
            Action.S -> Ferry(f.x, f.y + ins.value, f.direction)
            Action.W -> Ferry(f.x - ins.value, f.y, f.direction)
            else -> Ferry(f.x, f.y - ins.value, f.direction)
        }
    }

    private fun turnFerry(ins: Instruction, f: Ferry): Ferry {
        return if (ins.action == Action.R) Ferry(f.x, f.y, f.direction.right(ins.value))
        else Ferry(f.x, f.y, f.direction.left(ins.value))
    }

    private fun propelFerry(ins: Instruction, f: Ferry): Ferry {
        return when (f.direction) {
            Direction.E -> Ferry(f.x + ins.value, f.y, f.direction)
            Direction.W -> Ferry(f.x - ins.value, f.y, f.direction)
            Direction.N -> Ferry(f.x, f.y - ins.value, f.direction)
            else -> Ferry(f.x, f.y + ins.value, f.direction)
        }
    }

    private fun parseInstructions(input: List<String>): List<Instruction> {
        return input
            .map {
                Instruction(Action.valueOf(it.take(1)), it.drop(1).toInt())
            }
    }

    data class Ferry(val x: Int, val y: Int, val direction: Direction, val wayPoint: WayPoint = WayPoint(10, 1))
    data class Instruction(val action: Action, val value: Int)
    data class WayPoint(val x: Int, val y: Int) {
        fun rotateCW(degrees: Int): WayPoint {
            return (1..degrees / 90).fold(this) { w, _ -> WayPoint(w.y, -w.x) }
        }

        fun rotateCCW(degrees: Int): WayPoint {
            return (1..degrees / 90).fold(this) { w, _ -> WayPoint(-w.y, w.x) }
        }
    }

    enum class Action {
        E, S, W, N, F, L, R
    }

    enum class Direction {
        E, S, W, N; // Order matters

        fun right(degrees: Int): Direction {
            return listOf(E, S, W, N)[(this.ordinal + degrees / 90) % 4]
        }

        fun left(degrees: Int): Direction {
            return listOf(N, W, S, E)[((3 - this.ordinal) + degrees / 90) % 4]
        }
    }

}
