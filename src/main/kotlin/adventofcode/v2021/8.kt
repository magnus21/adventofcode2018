package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {
        val notes = parseInput(FileParser.getFileRows(2021, "8.txt"))

        val time1 = measureTimeMillis {
            val uniquePatternOccurrences =
                notes.sumOf { it.outputs.filter { output -> output.size in setOf(2, 3, 4, 7) }.count() }
            println("answer part 1: $uniquePatternOccurrences")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val displayValues = notes.map { note ->
                val mapping = mutableMapOf<Int, Set<Char>>()
                // Find unique pattern matches.
                mapping[1] = note.signalPatterns.first { it.size == 2 }
                mapping[7] = note.signalPatterns.first { it.size == 3 }
                mapping[4] = note.signalPatterns.first { it.size == 4 }
                mapping[8] = note.signalPatterns.first { it.size == 7 }
                mapping[3] = note.signalPatterns.filter { it.size == 5 }.first { (mapping[1]!! intersect it).size == 2 }
                mapping[9] = note.signalPatterns.filter { it.size == 6 }.first { (mapping[3]!! intersect it).size == 5 }
                mapping[6] = note.signalPatterns.filter { it.size == 6 }.first { (mapping[1]!! intersect it).size == 1 }
                mapping[0] = note.signalPatterns.filter { it.size == 6 }.first { it != mapping[9] && it != mapping[6] }
                mapping[5] = note.signalPatterns.filter { it.size == 5 }
                    .first { it != mapping[3] && (mapping[6]!! intersect it).size == 5 }
                mapping[2] = note.signalPatterns.filter { it.size == 5 }
                    .first { it != mapping[3] && (mapping[6]!! intersect it).size == 4 }

                note.outputs
                    .map { mapping.filter { m -> m.value == it }.map { it.key }.first() }
                    .joinToString("").toInt()
            }
            println("answer part 2: ${displayValues.sum()}")
        }
        println("Time: $time2 ms")
    }

    private fun parseInput(rows: List<String>): List<Note> {
        return rows.map { row ->
            val parts = row.split("|")
            val signalPatterns = parts[0].trim().split(" ")
            val outputs = parts[1].trim().split(" ")
            Note(signalPatterns.map { it.toSet() }, outputs.map { it.toSet() })
        }
    }

    data class Note(val signalPatterns: List<Set<Char>>, val outputs: List<Set<Char>>)
}