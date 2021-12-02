package adventofcode.v2021

import adventofcode.util.FileParser
import adventofcode.v2021.Day2.Command.*
import kotlin.system.measureTimeMillis

object Day2 {

    @JvmStatic
    fun main(args: Array<String>) {

        val commands = FileParser.getFileRows(2021, "2.txt")
            .map { it.split(" ") }
            .map { Pair(Command.valueOf(it[0].uppercase()), Integer.valueOf(it[1])) }

        val time1 = measureTimeMillis {
            val position = Position(0, 0)
            commands.forEach {
                when (it.first) {
                    FORWARD -> position.x += it.second
                    DOWN -> position.y += it.second
                    UP -> position.y -= it.second
                }
            }
            println("answer part 1: ${position.x * position.y}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val position = Position(0, 0)
            var aim = 0
            commands.forEach {
                when (it.first) {
                    FORWARD -> {
                        position.x += it.second
                        position.y += it.second * aim
                    }
                    DOWN -> aim += it.second
                    UP -> aim -= it.second
                }
            }
            println("answer part 2: ${position.x * position.y}")
        }
        println("Time: $time2 ms")
    }

    data class Position(var x: Int, var y: Int)

    enum class Command {
        FORWARD,
        DOWN,
        UP
    }
}