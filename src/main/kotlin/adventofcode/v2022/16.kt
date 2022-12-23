package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.time.ExperimentalTime

object Day16 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val valves = parseInput(FileParser.getFileRows(2022, "16.txt"))

        // to low: 1770, 1780
        //printResult("part 1") { part1(valves) }
        printResult("part 2") { part2(valves) }
    }

    private fun part1(inputValves: List<ValveRoom>): Int {
        val valveMap = inputValves.associateBy { it.label }

        val valves = inputValves.map { valve ->
            val expandedLeadsTo = connectToNonZeroValves(valve, setOf(valve.label), valveMap, 1)
                .sortedBy {
                    val v = valveMap[it.first]!!
                    v.rate
                }
                .toMutableList()

            val shortestPathsPerRoom = expandedLeadsTo.groupBy { it.first }
                .map { tunnel -> Pair(tunnel.key, tunnel.value.minOf { it.second }) }
                .toMutableList()

            ValveRoom(valve.label, valve.rate, shortestPathsPerRoom)
        }

        return findShortestPathPart1(valves)
    }

    private fun part2(inputValves: List<ValveRoom>): Int {
        val valveMap = inputValves.associateBy { it.label }

        val valves = inputValves.map { valve ->
            val expandedLeadsTo = connectToNonZeroValves(valve, setOf(valve.label), valveMap, 1)
                .sortedBy {
                    val v = valveMap[it.first]!!
                    v.rate
                }
                .toMutableList()

            val shortestPathsPerRoom = expandedLeadsTo.groupBy { it.first }
                .map { tunnel -> Pair(tunnel.key, tunnel.value.minOf { it.second }) }
                .toMutableList()

            ValveRoom(valve.label, valve.rate, shortestPathsPerRoom)
        }

        val optimizedValvesMap = valves.associateBy { it.label }

        val nonZeroRateValves = valves.filter { it.rate > 0 }.sortedBy { it.rate }.reversed()
        val nonOpenedValves = mutableListOf<ValveRoom>()
        nonZeroRateValves.forEach(nonOpenedValves::add)

        val myTrail = mutableListOf(TrailStep("AA", 0, 0, 0))
        val elephantTrail = mutableListOf(TrailStep("AA", 0, 0, 0))

        val totalTime = 26
        var myTime = 0
        var elephantTime = 0
        while (nonOpenedValves.isNotEmpty() && myTime < totalTime && elephantTime < totalTime) {

            val myChoices = nonOpenedValves.map {
                findShortestPathPartToValve(myTrail.last(), it, optimizedValvesMap)
            }.map { it.trail.last() }

            val myPath = myChoices
                .sortedBy {
                    (totalTime - (myTime + it.time + 1)) * optimizedValvesMap[it.valve]!!.rate
                }
                .reversed()
                .first()


            myTime = openValve(myPath, myTime, totalTime, myTrail)
            if (myTime < totalTime) {
                nonOpenedValves.remove(optimizedValvesMap[myPath.valve]!!)
            }


            val elephantsChoices = nonOpenedValves.map {
                findShortestPathPartToValve(elephantTrail.last(), it, optimizedValvesMap)
            }.map { it.trail.last() }

            val elephantsPath = elephantsChoices
                .sortedBy {
                    (totalTime - (elephantTime + it.time + 1)) * optimizedValvesMap[it.valve]!!.rate
                }
                .reversed()
                .firstOrNull()

            if (elephantsPath != null) {
                elephantTime = openValve(elephantsPath, elephantTime, totalTime, elephantTrail)
                if (elephantTime < totalTime) {
                    nonOpenedValves.remove(optimizedValvesMap[elephantsPath.valve]!!)
                }
            }
        }

        return findShortestPathPart2(valves)
    }

    private fun openValve(
        destination: TrailStep,
        startTime: Int,
        totalTime: Int,
        trail: MutableList<TrailStep>
    ): Int {
        var time = startTime
        val newTime = time + destination.time
        val last = trail.last()
        if (newTime < totalTime) {
            time += destination.time
            trail.add(
                TrailStep(
                    destination.valve,
                    destination.time + 1,
                    destination.nextRate,
                    last.totalRate + last.nextRate * (totalTime - time),
                    true
                )
            )
            return newTime + 1
        } else {
            println("Didn't reach valve in time")
            trail.add(
                TrailStep(
                    last.valve,
                    destination.time -(newTime - totalTime),
                    last.nextRate,
                    last.totalRate + last.nextRate * (totalTime - time)
                )
            )
            return totalTime
        }
    }

    private fun findShortestPathPartToValve(
        startValve: TrailStep,
        toValve: ValveRoom,
        valveMap: Map<String, ValveRoom>
    ): ValvePath {
        val queue = Queue<ValvePath>()
        queue.enqueue(ValvePath(listOf(TrailStep(startValve.valve, 0, 0, 0))))

        var shortestTime = Int.MAX_VALUE
        var shortestPath: ValvePath? = null
        val visited = mutableMapOf<String, Int>()
        visited[startValve.valve] = 0

        while (queue.isNotEmpty()) {
            val from = queue.dequeue()!!
            val trail = from.trail
            val startStep = trail.last()
            val valveRoom = valveMap[startStep.valve]!!
            val newPaths = valveRoom.leadsTo.map {
                val step = TrailStep(
                    it.first,
                    startStep.time + it.second,
                    valveMap[it.first]!!.rate,
                    0
                )
                ValvePath(trail.plus(step), setOf())
            }

            newPaths.forEach {
                val lastStep = it.trail.last()
                if (lastStep.valve == toValve.label && lastStep.time < shortestTime) {
                    shortestTime = lastStep.time
                    shortestPath = it
                }

                if (lastStep.time < visited.getOrDefault(lastStep.valve, Int.MAX_VALUE)) {
                    queue.enqueue(it)
                    visited[lastStep.valve] = lastStep.time
                }
            }
        }

        return shortestPath!!
    }


    private fun connectToNonZeroValves(
        valve: ValveRoom,
        visitedValves: Set<String>,
        valveMap: Map<String, ValveRoom>,
        length: Int
    ): List<Triple<String, Int, List<String>>> {

        return valve.leadsTo
            .filter { !visitedValves.contains(it.first) }
            .flatMap { leadToValve ->
                val room = valveMap[leadToValve.first]!!
                if (room.rate > 0) {
                    listOf(Triple(leadToValve.first, length, visitedValves.toList()))
                } else {
                    connectToNonZeroValves(
                        room,
                        visitedValves.plus(leadToValve.first),
                        valveMap,
                        length + 1
                    )
                }
            }
    }

    private data class TrailStep(
        val valve: String,
        val time: Int,
        val nextRate: Int,
        val totalRate: Int,
        val openedValve: Boolean = false
    )

    private data class ValvePath(
        val trail: List<TrailStep>,
        val openedValves: Set<String> = setOf()
    )

    private fun findShortestPathPart1(valves: List<ValveRoom>, maxTime: Int = 30): Int {

        val maxRate = valves.sumOf { it.rate }

        val valveMap = valves.associateBy { it.label }
        val nonZeroRateValves = valves.count { it.rate > 0 }

        val start = ValvePath(listOf(TrailStep("AA", 0, 0, 0)))

        val queue = Queue<ValvePath>()
        queue.enqueue(start)

        var highestRateAtEnd = 0

        while (queue.isNotEmpty()) {
            val from = queue.dequeue()!!
            val trail = from.trail
            val lastStep = trail.last()
            val secondLastStep = trail.dropLast(1).lastOrNull()
            val valveRoom = valveMap[lastStep.valve]!!

            val newPaths = valveRoom.leadsTo
                .flatMap {
                    val newRoom = valveMap[it.first]!!
                    val timeToNewRoom = it.second
                    val newSteps = mutableListOf<TrailStep>()
                    val newTimeDontOpenValve = lastStep.time + timeToNewRoom
                    val newTimeOpenValve = lastStep.time + timeToNewRoom + 1

                    if (newTimeOpenValve <= maxTime && !from.openedValves.contains(newRoom.label)) {
                        newSteps.add(
                            TrailStep(
                                newRoom.label,
                                newTimeOpenValve,
                                lastStep.nextRate + newRoom.rate,
                                lastStep.totalRate + (timeToNewRoom + 1) * lastStep.nextRate,
                                true
                            )
                        )
                    }

                    if (secondLastStep == null || newRoom.label != secondLastStep.valve || lastStep.openedValve) {
                        val time = if (newTimeDontOpenValve > maxTime) maxTime else newTimeDontOpenValve
                        val timeStep = if (newTimeDontOpenValve > maxTime) maxTime - lastStep.time else timeToNewRoom
                        val room = if (newTimeDontOpenValve > maxTime) valveRoom.label else newRoom.label
                        newSteps.add(
                            TrailStep(
                                room,
                                time,
                                lastStep.nextRate,
                                lastStep.totalRate + timeStep * lastStep.nextRate
                            )
                        )
                    }
                    newSteps
                }
                .map {
                    val newOpenedValves = if (it.openedValve) from.openedValves.plus(it.valve) else from.openedValves
                    ValvePath(trail.plus(it), newOpenedValves)
                }.filter { it.trail.last().time <= maxTime }

            newPaths
                .forEach {
                    val last = it.trail.last()
                    val projectedRate = last.totalRate + (maxTime - last.time) * last.nextRate
                    val projectedMax = last.totalRate + (maxTime - last.time) * maxRate

                    if ((nonZeroRateValves == it.openedValves.size) && highestRateAtEnd <= projectedRate) {
                        highestRateAtEnd = projectedRate
                        println("New high (all valves opened): $it, $projectedRate")
                    } else if (projectedMax > highestRateAtEnd) {
                        if (last.time < maxTime) {
                            queue.enqueue(it)
                        }
                        if (last.time == maxTime && highestRateAtEnd < last.totalRate) {
                            highestRateAtEnd = last.totalRate
                            println("New high: ${last.totalRate}, $it")
                        }
                    }
                }
            queue.sortQueue(compareBy<ValvePath> { it.openedValves.size }.reversed())
        }
        return highestRateAtEnd
    }

    private data class ValvePathWithElephant(
        val trails: Pair<List<TrailStep>, List<TrailStep>>,
        val openedValves: Set<String> = setOf()
    )

    private fun findShortestPathPart2(valves: List<ValveRoom>, maxTime: Int = 26): Int {

        val maxRate = valves.sumOf { it.rate }
        val valveMap = valves.associateBy { it.label }
        val nonZeroRateValves = valves.count { it.rate > 0 }

        val start = ValvePathWithElephant(
            Pair(
                listOf(TrailStep("AA", 0, 0, 0)),
                listOf(TrailStep("AA", 0, 0, 0))
            ),
            setOf()
        )

        val queue = Queue<ValvePathWithElephant>()
        queue.enqueue(start)

        var highestRateAtEnd = 0

        while (queue.isNotEmpty()) {
            val from = queue.dequeue()!!

            /*val newPaths = getNewPaths(from, valveMap, maxTime)

            newPaths
                .forEach {
                    val last = it.trail.last()
                    val projectedRate = last.totalRate + (maxTime - last.time) * last.nextRate
                    val projectedMax = last.totalRate + (maxTime - last.time) * maxRate

                    if ((nonZeroRateValves == it.openedValves.size) && highestRateAtEnd <= projectedRate) {
                        highestRateAtEnd = projectedRate
                        println("New high (all valves opened): $it, $projectedRate")
                    } else if (projectedMax > highestRateAtEnd) {
                        if (last.time < maxTime) {
                            queue.enqueue(it)
                        }
                        if (last.time == maxTime && highestRateAtEnd < last.totalRate) {
                            highestRateAtEnd = last.totalRate
                            println("New high: ${last.totalRate}, $it")
                        }
                    }
                }
            queue.sortQueue(compareBy<ValvePath> { it.openedValves.size }.reversed())
            */
        }
        return highestRateAtEnd
    }


    private fun tryOpenValve(
        leadTo: Pair<String, Int>,
        lastStep: TrailStep,
        valveMap: Map<String, ValveRoom>,
        openedValves: Map<String, Int>,
        maxTime: Int
    ): TrailStep? {
        val newRoom = valveMap[leadTo.first]!!
        val timeToNewRoom = leadTo.second
        val newTimeOpenValve = lastStep.time + timeToNewRoom + 1

        if (newTimeOpenValve <= maxTime && !openedValves.contains(newRoom.label)) {
            return TrailStep(
                newRoom.label,
                newTimeOpenValve,
                lastStep.nextRate + newRoom.rate,
                lastStep.totalRate + (timeToNewRoom + 1) * lastStep.nextRate,
                true
            )
        }
        return null
    }

    private fun getNewPaths(
        from: ValvePathWithElephant,
        openedValves: Map<String, Int>,
        valveMap: Map<String, ValveRoom>,
        maxTime: Int
    ) {

        /* val lastStep = from.trails.first.last()
         val secondLastStep = from.trails.first.dropLast(1).lastOrNull()
         val lastRoom = valveMap[lastStep.valve]!!

         val elephantLastStep = from.trails.second.last()
         val elephantSecondLastStep = from.trails.second.dropLast(1).lastOrNull()
         val elephantLastRoom = valveMap[elephantLastStep.valve]!!

         val newPaths = lastRoom.leadsTo
             .flatMap { leadTo ->
                 val newSteps = mutableListOf<Pair<TrailStep,TrailStep>>()

                 val yourNextStep = tryOpenValve(leadTo, lastStep,valveMap, openedValves, maxTime)
                 if(yourNextStep != null) {

                     elephantLastRoom.leadsTo.flatMap {
                         val elephantsNextSteps = tryOpenValve(it, elephantLastStep, valveMap, openedValves, maxTime)
                         //elephantsNextSteps
                     }
                 }


                 val newRoom = valveMap[leadTo.first]!!
                 val timeToNewRoom = leadTo.second
                 val newTimeOpenValve = lastStep.time + timeToNewRoom + 1

                 if (newTimeOpenValve <= maxTime && !openedValves.contains(newRoom.label)) {
                    val yourStep =  TrailStep(
                             newRoom.label,
                             newTimeOpenValve,
                             lastStep.nextRate + newRoom.rate,
                             lastStep.totalRate + (timeToNewRoom + 1) * lastStep.nextRate,
                             true
                         )
                     /*elephantLastRoom.leadsTo
                         .filter { it.first != newRoom.label }
                         .map {

                         }*/



                 }

                 val newTimeDontOpenValve = lastStep.time + timeToNewRoom
                 if (secondLastStep == null || newRoom.label != secondLastStep.valve || lastStep.openedValve) {
                     val time = if (newTimeDontOpenValve > maxTime) maxTime else newTimeDontOpenValve
                     val timeStep = if (newTimeDontOpenValve > maxTime) maxTime - lastStep.time else timeToNewRoom
                     val room = if (newTimeDontOpenValve > maxTime) lastRoom.label else newRoom.label
                     newSteps.add(
                         TrailStep(
                             room,
                             time,
                             lastStep.nextRate,
                             lastStep.totalRate + timeStep * lastStep.nextRate
                         )
                     )
                 }
                 newSteps
             }
             .map {
                 val newOpenedValves = if (it.openedValve) openedValves.plus(it.valve) else openedValves
                 ValvePath(trail.plus(it), newOpenedValves)
             }.filter { it.trail.last().time <= maxTime }
         return newPaths
         */
    }

    private data class ValveRoom(val label: String, val rate: Int, val leadsTo: MutableList<Pair<String, Int>>)

    private fun parseInput(rows: List<String>): List<ValveRoom> {
        return rows.map { row ->
            "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z,\\s]+)".toRegex()
                .matchEntire(row)?.destructured
                ?.let { (label, rate, leadsTo) ->
                    ValveRoom(
                        label,
                        rate.toInt(),
                        leadsTo.replace(" ", "").split(",").map { Pair(it, 1) }.toMutableList()
                    )
                } ?: throw IllegalArgumentException("Bad input '$row'")

        }
    }
}