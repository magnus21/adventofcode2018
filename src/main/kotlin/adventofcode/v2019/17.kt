package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day17 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "17.txt").map(String::toLong)

        val map = mutableMapOf<Position, Char>()
        // Run program.
        val time1 = measureTimeMillis {
            val result = IntCodeComputer(input.toMutableList()).runWithInput(listOf())
                .first.map(Long::toChar)

            var y = 0
            var x = 0

            result.forEach {
                if (it.toInt() == 10) {
                    x = 0
                    y++
                } else {
                    map[Position(x++, y)] = it
                }
            }

            val alignmentParametersSum =
                map.entries.filter { isInterSection(it.key, map) }.map { it.key.x * it.key.y }.sum()

            println("Answer part 1: $alignmentParametersSum")

            printMap(map)
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            // Find naive program.
            val naiveInputpCode = findNaiveProgram(map)
            println(naiveInputpCode)

            // A: L12,L10,R8,L12,R8,R10,R12
            // B: L10,R12,R8
            // C: R8,R10,R12
            // Main routine: A,A,B,B,C,A,B

            val mainRoutine = "A,C,A,C,B,B,C,A,C,B"
            val aRoutine = "L,12,L,10,R,8,L,12"
            val bRoutine = "L,10,R,12,R,8"
            val cRoutine = "R,8,R,10,R,12"

            val asciiInput = convertToAsciiRoutine(mainRoutine) +
                    convertToAsciiRoutine(aRoutine) +
                    convertToAsciiRoutine(bRoutine) +
                    convertToAsciiRoutine(cRoutine) + listOf('n'.toInt().toLong(),10) // No video feed :)

            println(asciiInput)

            val program = input.toMutableList()
            program[0] = 2
            val result = IntCodeComputer(program).runWithInput(asciiInput)

            result.first.map(Long::toChar).filter { it != ',' }.forEach(::print)

            println("Exit code: ${result.second}")
            println("Dust: ${result.first.takeLast(1)}")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun convertToAsciiRoutine(routine: String): List<Long> {

        val asciiRoutine = routine.toCharArray().map { it.toInt().toLong() }.toMutableList()
        asciiRoutine.add(10) // newline

        return asciiRoutine
    }

    private fun findNaiveProgram(map: MutableMap<Position, Char>): String {
        var currentPosition = map.entries.filter { it.value == '^' }.map { it.key }.first()
        var direction = Position(-1, 0)


        var previousTurn = 'L'
        var stepCount = 0
        var program = "L"
        while (true) {
            val (turn, pos, didTurn) = getNextPosition(currentPosition, direction, map, previousTurn)

            if (turn == null) {
                program += stepCount.toString()
                break
            }

            currentPosition = pos

            if (!didTurn) {
                stepCount++
            } else {
                direction = when (direction) {
                    Position(-1, 0) -> if (turn == 'L') Position(0, 1) else Position(0, -1)
                    Position(1, 0) -> if (turn == 'L') Position(0, -1) else Position(0, 1)
                    Position(0, 1) -> if (turn == 'L') Position(1, 0) else Position(-1, 0)
                    else -> if (turn == 'L') Position(-1, 0) else Position(1, 0)
                }
                program += (stepCount.toString() + "," + turn)
                previousTurn = turn
                stepCount = 1
            }
        }
        return program
    }

    private fun getNextPosition(
        currentPos: Position,
        direction: Position,
        map: MutableMap<Position, Char>,
        previousTurn: Char
    ): Triple<Char?, Position,Boolean> {

        val forwardPos = Position(currentPos.x + direction.x, currentPos.y + direction.y)
        if (map[forwardPos] == '#') {
            return Triple(previousTurn, Position(currentPos.x + direction.x, currentPos.y + direction.y), false)
        }

        val leftDir = if (direction.x != 0) Position(0, -direction.x) else Position(direction.y,0)
        val leftPos = Position(currentPos.x + leftDir.x, currentPos.y + leftDir.y)
        if (map[leftPos] == '#') {
            return Triple('L', leftPos,true)
        }

        val rightDir = if (direction.y != 0) Position(-direction.y, 0) else Position(0,direction.x)
        val rightPos = Position(currentPos.x + rightDir.x, currentPos.y + rightDir.y)
        if (map[rightPos] == '#') {
            return Triple('R', rightPos, true)
        }

        return Triple(null, currentPos, false)

    }

    private fun isInterSection(position: Position, map: MutableMap<Position, Char>): Boolean {
        return map[Position(position.x - 1, position.y)] == '#' &&
                map[Position(position.x + 1, position.y)] == '#' &&
                map[Position(position.x, position.y - 1)] == '#' &&
                map[Position(position.x, position.y + 1)] == '#'
    }

    private fun printMap(map: MutableMap<Position, Char>) {

        val xSpan = Pair(map.keys.minOf { it.x }, map.keys.maxOf { it.x })
        val ySpan = Pair(map.keys.minOf { it.y }, map.keys.maxOf { it.y })

        for (y in ySpan.first..ySpan.second) {
            for (x in xSpan.first..xSpan.second) {
                val tile = map[Position(x, y)]
                print(tile!!)
            }
            println()
        }
    }

    private data class Position(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }
}