package adventofcode.v2020

import adventofcode.util.FileParser

object Day5 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2020, "5.txt")

        val seatIds = part1(input)
        part2Imperative(seatIds)
        part2Functional(seatIds)
    }

    private fun part1(boardingPasses: List<String>): List<Int> {

        val seatIds = boardingPasses.map {
            val row = getSeatPosition(it.take(7), 128)
            val seat = getSeatPosition(it.drop(7), 8)

            val seatId = row * 8 + seat
            //println("$it: $row  $seat, $seatId")
            seatId
        }

        println("Part 1: ${seatIds.max()}")

        return seatIds
    }

    private fun part2Imperative(seatIds: List<Int>) {
        var lastSeatId = Int.MAX_VALUE
        for (seatId in seatIds.sorted()) {
            if (seatId - 1 > lastSeatId) {
                println("Part 2: " + (seatId - 1))
                break
            }
            lastSeatId = seatId
        }
    }

    private fun part2Functional(seatIds: List<Int>) {
        println("Part 2: " + (seatIds.min()!!..seatIds.max()!!).subtract(seatIds).first())
    }


    private fun getSeatPosition(boardingPassPart: String, size: Int): Int {

        return boardingPassPart.fold(Pair(0, size - 1)) { interval, letter ->
            when (letter) {
                'F', 'L' -> Pair(interval.first, getMiddleOfInterval(interval))
                else -> Pair(getMiddleOfInterval(interval) + 1, interval.second)
            }
        }.first
    }

    private fun getMiddleOfInterval(interval: Pair<Int, Int>) =
        interval.first + (interval.second - interval.first) / 2

}


