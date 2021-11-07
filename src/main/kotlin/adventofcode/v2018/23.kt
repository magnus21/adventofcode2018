package adventofcode.v2018

import java.io.File

fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/23.txt").readLines()

    Day23.run(rawInput)
}

object Day23 {
    data class Position(val x: Int, val y: Int, val z: Int)
    data class NanoBot(val position: Position, val radius: Int)

    fun run(rawInput: List<String>) {

        val nanoBots = parseInput(rawInput)
        println(nanoBots.sortedByDescending { it.radius }.take(5))

        val maxNanoBot = nanoBots.maxByOrNull { it.radius }!!
        println(maxNanoBot)

        val nanoBotsInRange =
            nanoBots.filter { distanceBetween(it.position, maxNanoBot.position) <= maxNanoBot.radius }.count()
        println(nanoBotsInRange)

        // Part two

        // Start with good guess, weighted average.
        val weightedAverageX =
            nanoBots.flatMap { (1..(maxNanoBot.radius / it.radius)).map { _ -> it.position.x } }.average().toInt()
        val weightedAverageY =
            nanoBots.flatMap { (1..(maxNanoBot.radius / it.radius)).map { _ -> it.position.y } }.average().toInt()
        val weightedAverageZ =
            nanoBots.flatMap { (1..(maxNanoBot.radius / it.radius)).map { _ -> it.position.z } }.average().toInt()

        val averagePosition = Position(weightedAverageX, weightedAverageY, weightedAverageZ)
        val nanoBotsInRangeFrom = getNanoBotsInRange(nanoBots, averagePosition)

        // Home in search.
        var goodGuessPos = averagePosition //Position(x=44884402, y=52508524, z=28258248)//Position(x=43884402, y=53508524, z=27658248)//Position(x=42884402, y=54508524, z=27658248)//Position(x=41984402, y=55508524, z=26858248)//Position(x=41084402, y=55508524, z=27758248)//Position(x=40084402, y=55508524, z=28758248)//Position(x=39184402, y=55508524, z=29658248)//Position(x=38384402, y=55608524, z=30558248)//Position(x=37484402, y=55708524, z=31558248)//Position(x=36484402, y=55608524, z=32558248)//Position(x=35584402, y=55708524, z=33558248)//Position(x=34684402, y=56508524, z=34558248) //Position(x=34684402, y=56508524, z=34558248)// Position(x=33684402, y=57108524, z=35558248)//Position(x=32684402, y=58108524, z=35558248)//Position(x=32634402, y=58208524, z=35458248)
        var goodGuessValue = nanoBotsInRangeFrom //847
        var step = 100000
        do {
            do {
                val guessMap = mutableMapOf<Position, Int>()
                for (x in (goodGuessPos.x - 10*step)..(goodGuessPos.x + 10*step) step step) {
                    for (y in (goodGuessPos.y - 10*step)..(goodGuessPos.y + 10*step) step step) {
                        for (z in (goodGuessPos.z - 10*step)..(goodGuessPos.z + 10*step) step step) {
                            val pos = Position(x, y, z)
                            guessMap.put(pos, getNanoBotsInRange(nanoBots, pos))
                        }
                    }
                }
                val newMax = guessMap.toList().sortedBy { it.first.x + it.first.y + it.first.z }.maxByOrNull { it.second }!!
                println(newMax)

                if (newMax.second <= goodGuessValue) {
                    println("Max for step: $newMax $step")
                    break
                    //Position(x=51384402, y=47808524, z=25158248)=892
                }

                goodGuessPos = newMax.first
                goodGuessValue = newMax.second

            } while (true)
            step /= 10
        } while(step >= 1)

        // Fine net search.
        val result = mutableListOf<Pair<Position,Int>>()
        val range = 10 // Check that this is big enough.
        for (x in (goodGuessPos.x - range)..(goodGuessPos.x + range) step 1) {
            for (y in (goodGuessPos.y - range)..(goodGuessPos.y + range) step 1) {
                for (z in (goodGuessPos.z - range)..(goodGuessPos.z + range) step 1) {
                    val pos = Position(x, y, z)
                    result.add(Pair(pos, getNanoBotsInRange(nanoBots, pos)))
                }
            }
        }

        // Answer, hopefully..
        println(result.filter { it.second == goodGuessValue }
            .sortedBy { it.first.x + it.first.y + it.first.z }
            .map { it.first.x + it.first.y + it.first.z }
            .first()
        )
    }

    private fun getNanoBotsInRange(
        nanoBots: List<NanoBot>,
        position: Position
    ) = nanoBots.filter { distanceBetween(position, it.position) <= it.radius }.count()

    private fun distanceBetween(position1: Position, position2: Position): Int {
        return Math.abs(position1.x - position2.x) +
                Math.abs(position1.y - position2.y) +
                Math.abs(position1.z - position2.z)
    }

    private fun parseInput(rawInput: List<String>): List<NanoBot> {
        return rawInput.map {
            val matchResults = "([-]?\\d+)".toRegex().findAll(it).toList().map { str -> Integer.valueOf(str.value) }

            NanoBot(
                Position(
                    matchResults[0],
                    matchResults[1],
                    matchResults[2]
                ), matchResults[3]
            )
        }
    }


}