package adventofcode.v2018

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {

    val input = File("src/main/resources/12.txt").readLines()

    val initialState = input[0].replace("initial state: ", "").toList()
    val spreadPatterns = input.drop(2).map { parsePatterns(it) }

    val patternSize = 5
    val nrOfGenerations = 50000000000L //20
    val dot = '.'

    var recentState = State(initialState, 0)
    var generation = 1
    while (generation <= nrOfGenerations) {

        val plants = mutableListOf<Char>()

        for (position in 0..(recentState.plants.size - 1)) {
            val patternForPosition = getPatternForPosition(position, recentState, patternSize)
            plants.add(getMatchResult(patternForPosition, spreadPatterns))
        }

        val possiblePrefix = mutableListOf<Char>()
        for (position in -patternSize..-1) {
            val inputPosition = max(position, -3)
            val patternForPosition = getPatternForPosition(inputPosition, recentState, patternSize)
            possiblePrefix.add(getMatchResult(patternForPosition, spreadPatterns))
        }
        val actualPrefix = possiblePrefix.dropWhile { it == dot }
        plants.addAll(0, actualPrefix)

        val possiblePostFix = mutableListOf<Char>()
        for (position in (recentState.plants.size + patternSize - 1) downTo recentState.plants.size) {
            val inputPosition = min(position, recentState.plants.size + 2)
            val patternForPosition = getPatternForPosition(inputPosition, recentState, patternSize)
            possiblePostFix.add(getMatchResult(patternForPosition, spreadPatterns))
        }
        plants.addAll(possiblePostFix.dropWhile { it == dot }.reversed())

        val newOffset = recentState.zeroOffset + actualPrefix.size

        if (generation % 1 == 0) {
            print("\n$generation: ".padStart(4, ' '))
            plants.forEach { print(it) }
            print(" Offset: $newOffset")
        }

        val newState = State(plants, newOffset)

        val plantsLeftTrimmed = plants.joinToString("").trimStart('.')
        if (recentState.plants.joinToString("").trimStart('.') == plantsLeftTrimmed) {
            recentState = newState
            break
        }

        recentState = newState

        generation++
    }

    val generationOffset = nrOfGenerations - generation // For Part 1 remove this since generation++ is done once more.
    val result = recentState.plants
        .mapIndexed { position, plant -> if (plant != dot) position - recentState.zeroOffset + generationOffset else 0 }
        .sum()

    println("\nResult: $result")
}

fun getMatchResult(patternForPosition: String, spreadPatterns: List<SpreadPattern>): Char {
    return spreadPatterns
        .filter { it.pattern == patternForPosition }
        .map { it.outCome }
        .getOrElse(0) { '.' }
}

fun getPatternForPosition(position: Int, state: State, patternSize: Int): String {
    return if (position < 2) {
        state.plants.take(3 + position).joinToString("").padStart(patternSize, '.')
    } else if (position >= 2 && position < state.plants.size - 2) {
        state.plants.drop(position - 2).take(patternSize).joinToString("")
    } else {
        state.plants.takeLast(state.plants.size - position + 2).joinToString("").padEnd(patternSize, '.')
    }
}

data class SpreadPattern(val pattern: String, val outCome: Char)
data class State(val plants: List<Char>, val zeroOffset: Int)

fun parsePatterns(pattern: String): SpreadPattern {
    val parts = pattern.split(" => ")
    return SpreadPattern(parts[0], parts[1].toCharArray()[0])
}
