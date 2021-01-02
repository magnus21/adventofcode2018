package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "16.txt")
        val ticketNotes = parseInput(input)

        val time = measureTimeMillis {
            println("Part 1: ${part1(ticketNotes)}")
            println("Part 2: ${part2(ticketNotes)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(ticketNotes: TicketNotes): Int {
        val validRanges = ticketNotes.fields.map { it.second }.flatten()
        return ticketNotes.nearbyTickets.flatten()
            .filter { v -> validRanges.none { it.contains(v) } }
            .sum()
    }

    private fun part2(ticketNotes: TicketNotes): Long {
        val validRanges = ticketNotes.fields.map { it.second }.flatten()
        val validTickets = ticketNotes.nearbyTickets
            .filter { ticket -> ticket.all { n -> validRanges.any { r -> r.contains(n) } } }

        val possiblePositionsForField = ticketNotes.fields.map { field ->
            Pair(
                field.first,
                ticketNotes.fields.indices.filter { pos ->
                    validTickets
                        .map { it[pos] }
                        .all { n -> field.second.any { r -> r.contains(n) } }
                }
            )
        }

        val pickedField = mutableSetOf<Int>()
        val mapping = possiblePositionsForField
            .sortedBy { it.second.size }
            .map {
                val chosen = it.second.first { p -> !pickedField.contains(p) }
                pickedField.add(chosen)
                Pair(chosen, it.first)
            }.toMap()

        return ticketNotes.myTicket
            .filterIndexed { i, _ -> (mapping[i] ?: error("Can't find mapping!")).startsWith("departure") }.fold(1L) { acc, n -> acc * n }
    }

    private fun parseInput(input: List<String>): TicketNotes {

        val fields = mutableListOf<Pair<String, List<IntRange>>>()
        val myTicket = mutableListOf<Int>()
        val nearbyTickets = mutableListOf<List<Int>>()

        var section = 1
        input.filter { it != "your ticket:" && it != "nearby tickets:" }.forEach {
            if (it == "") {
                section++
            } else {
                when (section) {
                    1 -> {
                        val parts = it.split(":")
                        fields.add(Pair(parts[0], parts[1].split("or").map { range ->
                            val rangeParts = range.trim().split("-")
                            IntRange(rangeParts[0].toInt(), rangeParts[1].toInt())
                        }))
                    }
                    2 -> myTicket.addAll(it.split(",").map(String::toInt))
                    else -> nearbyTickets.add(it.split(",").map(String::toInt))
                }
            }
        }

        return TicketNotes(fields, myTicket, nearbyTickets)
    }

    data class TicketNotes(
        val fields: List<Pair<String, List<IntRange>>>,
        val myTicket: List<Int>,
        val nearbyTickets: List<List<Int>>
    )
}