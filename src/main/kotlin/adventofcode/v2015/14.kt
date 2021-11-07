package adventofcode.v2015

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2015, "14.txt")

        val reindeers = parseInput(input)

        val time1 = measureTimeMillis {

            val timeTargetInSeconds = 2503
            val result = mutableMapOf<String, Int>()
            for (sec in 1..timeTargetInSeconds) {
                reindeers.filter { isFlying(sec, it) }
                    .forEach { result[it.name] = result.getOrDefault(it.name, 0) + it.speed }
            }
            val answer = result.maxByOrNull { it.value }

            println("Part 1: $answer ")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            val timeTargetInSeconds = 2503
            val result = mutableMapOf<String, Int>()
            val points = mutableMapOf<String, Int>()
            for (sec in 1..timeTargetInSeconds) {
                reindeers.filter { isFlying(sec, it) }
                    .forEach { result[it.name] = result.getOrDefault(it.name, 0) + it.speed }

                val leadingDistance = result.map { it.value }.maxOrNull()!!

                result.filter { it.value == leadingDistance }.forEach {
                    points[it.key] = points.getOrDefault(it.key, 0) + 1
                }
            }
            val answer = points.maxByOrNull { it.value }

            println("Part 2: $answer ")
        }
        println("Time: $time2 ms")
    }

    data class Reindeer(val name: String, val speed: Int, val staminaInSeconds: Int, val restInSeconds: Int)

    private fun isFlying(sec: Int, reindeer: Reindeer): Boolean {
        val rest = sec % (reindeer.staminaInSeconds + reindeer.restInSeconds)
        return rest != 0 && rest <= reindeer.staminaInSeconds
    }

    private fun parseInput(input: List<String>): List<Reindeer> {
        return input.map {
            val parts = it.split(' ')
            Reindeer(parts[0], parts[3].toInt(), parts[6].toInt(), parts[13].toInt())
        }
    }
}