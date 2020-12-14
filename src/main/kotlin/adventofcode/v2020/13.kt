package adventofcode.v2020

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "13.txt")
        val (earliestTimestamp, departures) = parseInput(input)

        val earliestDeparture = departures
            .filter { it != "x" }
            .map { it.toInt() }
            .map { Pair(it, if (earliestTimestamp % it == 0) 0 else it - earliestTimestamp % it) }
            .minBy { it.second }!!

        val time1 = measureTimeMillis {

            println("Answer part 1: ${earliestDeparture.first * earliestDeparture.second}")
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {

            val deps = departures
                .mapIndexed { i, dep -> Pair(dep, i) }
                .filter { it.first != "x" }
                .map {
                    Triple(
                        it.first.toLong(),
                        it.second.toLong(),
                        AdventOfCodeUtil.leastCommonMultiple(it.second.toLong(), it.first.toLong())
                    )
                }

            println("Answer part 2: ${part2(deps)}")

            brutForce(deps)
        }
        println("Time part 2: ($time2 milliseconds)")

    }


    private fun part2(deps: List<Triple<Long, Long, Long>>): Long {
        var time = deps[0].first
        var step = deps[0].first
        deps.drop(1).forEach { dep ->
            while ((time + dep.second) % dep.first != 0L) {
                time += step;
            }
            step *= dep.first;
        }
        return time;
    };

    private fun brutForce(deps: List<Triple<Long, Long, Long>>) {
        val firstBus = deps[0]
        val cycles = deps.drop(1).map { dep ->
            println("####### ${dep.first} #######")
            var minute = 0L
            var prev = 0L
            var count = 0

            while (true) {
                if ((minute - dep.second) % firstBus.first == 0L) {
                    if (count == 1) {
                        println("========  ${minute}, ${minute - prev}  =========")
                        break
                    }
                    prev = minute
                    count++
                }

                minute += dep.first
            }
            Triple(dep, minute, minute - prev)
        }

        val cycle = cycles.maxBy { it.third }!!

        var minute = cycle.second //100000000023260L //cycle.second
        val cycleOffset = cycle.first.second
        while (true) {
            minute += cycle.third
            if (minute % 100000000 == 0L) {
                println("========  ${minute} =========")
            }

            if (deps.all { (minute - (cycleOffset - it.second)) % it.first == 0L }) {
                println("Minute: ${minute - cycleOffset}")
                break
            }
        }
        /*
        Minute: 825305207525452
        Time part 2: (1869257 milliseconds)
         */
    }

    private fun parseInput(input: List<String>): Pair<Int, List<String>> {
        return Pair(input[0].toInt(), input[1].split(","))
    }


}
