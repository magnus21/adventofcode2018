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

        printResult("part 1") { part1(valves) }
        // Brut forced part 2.
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

        return findShortestPathPart2(optimizedValvesMap, nonZeroRateValves)
    }

    private fun findShortestPathPartToValve(
        startValve: String,
        toValve: String,
        valveMap: Map<String, ValveRoom>
    ): ValvePath {
        val queue = Queue<ValvePath>()
        queue.enqueue(ValvePath(listOf(TrailStep(startValve, 0, 0, 0))))

        var shortestTime = Int.MAX_VALUE
        var shortestPath: ValvePath? = null
        val visited = mutableMapOf<String, Int>()
        visited[startValve] = 0

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
                if (lastStep.valve == toValve && lastStep.time < shortestTime) {
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

    private fun findShortestPathPart2(
        valveMap: Map<String, ValveRoom>,
        nonZeroValves: List<ValveRoom>,
        maxTime: Int = 26
    ): Int {

        val maxRate = nonZeroValves.sumOf { it.rate }
        val nonZeroRateValves = nonZeroValves.size

        val start = ValvePathWithElephant(
            Pair(
                listOf(TrailStep("AA", 0, 0, 0)),
                listOf(TrailStep("AA", 0, 0, 0))
            ),
            setOf()
        )

        val nonZeroValvesMap = nonZeroValves
            .plus(valveMap["AA"]!!)
            .map { vFrom ->
                val leadsTo = nonZeroValves
                    .filter { vFrom != it }
                    .map { vTo ->
                        val valvePath = findShortestPathPartToValve(vFrom.label, vTo.label, valveMap).trail.last()
                        Pair(vTo.label, valvePath.time)
                    }
                ValveRoom(vFrom.label, vFrom.rate, leadsTo.toMutableList())
            }.associateBy { it.label }

        val queue = Queue<ValvePathWithElephant>()
        queue.enqueue(start)

        var highestRateAtEnd = 0 //1971 //0

        while (queue.isNotEmpty()) {
            val from = queue.dequeue()!!
            val myLastStep = from.trails.first.last()
            val elephantsLastStep = from.trails.second.last()

            val newPaths = nonZeroValvesMap[myLastStep.valve]!!.leadsTo
                .filter { !from.openedValves.contains(it.first) }
                .sortedByDescending { nonZeroValvesMap[it.first]!!.rate }
                .flatMap { myLeadTo ->
                    val toValve = nonZeroValvesMap[myLeadTo.first]!!
                    val newTime = myLastStep.time + myLeadTo.second + 1

                    val myStep = if (newTime < maxTime) TrailStep(
                        toValve.label,
                        newTime,
                        myLastStep.nextRate + toValve.rate,
                        myLastStep.totalRate + myLastStep.nextRate * (myLeadTo.second + 1),
                        true
                    ) else TrailStep(
                        myLastStep.valve,
                        maxTime,
                        myLastStep.nextRate,
                        myLastStep.totalRate + myLastStep.nextRate * (maxTime - myLastStep.time)
                    )

                    if (from.openedValves.size + 1 == nonZeroRateValves) {
                        listOf(
                            ValvePathWithElephant(
                                Pair(from.trails.first.plus(myStep), from.trails.second),
                                from.openedValves.plus(myStep.valve)
                            )
                        )
                    } else {
                        val valvesLeft = nonZeroValvesMap[elephantsLastStep.valve]!!.leadsTo
                            .filter { !from.openedValves.contains(it.first) }
                            .sortedByDescending { nonZeroValvesMap[it.first]!!.rate }
                            .filter { it.first != myStep.valve }

                        if (valvesLeft.isEmpty()) {
                            listOf(
                                ValvePathWithElephant(
                                    Pair(from.trails.first.plus(myStep), from.trails.second),
                                    from.openedValves.plus(myStep.valve)
                                )
                            )
                        }
                        valvesLeft.map { eLeadTo ->
                            val eToValve = nonZeroValvesMap[eLeadTo.first]!!
                            val eNewTime = elephantsLastStep.time + eLeadTo.second + 1
                            if (eNewTime < maxTime) TrailStep(
                                eToValve.label,
                                eNewTime,
                                elephantsLastStep.nextRate + eToValve.rate,
                                elephantsLastStep.totalRate + elephantsLastStep.nextRate * (eLeadTo.second + 1),
                                true
                            ) else TrailStep(
                                elephantsLastStep.valve,
                                maxTime,
                                elephantsLastStep.nextRate,
                                elephantsLastStep.totalRate + elephantsLastStep.nextRate * (maxTime - elephantsLastStep.time)
                            )
                        }.map {
                            ValvePathWithElephant(
                                Pair(from.trails.first.plus(myStep), from.trails.second.plus(it)),
                                from.openedValves.plus(myStep.valve).plus(it.valve)
                            )
                        }
                    }
                }
            newPaths
                .forEach {
                    val myLast = it.trails.first.last()
                    val eLast = it.trails.second.last()

                    val projectedMax = myLast.totalRate + eLast.totalRate +
                            (maxTime - myLast.time) * maxRate +
                            (maxTime - eLast.time) * maxRate

                    val myTotalRate = myLast.totalRate + (maxTime - myLast.time) * myLast.nextRate
                    val eTotalRate = eLast.totalRate + (maxTime - eLast.time) * eLast.nextRate
                    val totalProjectedRate = myTotalRate + eTotalRate

                    if (highestRateAtEnd < totalProjectedRate) {
                        highestRateAtEnd = totalProjectedRate
                        println("New high: ${totalProjectedRate}, $it")
                    }

                    if (nonZeroRateValves == it.openedValves.size) {
                        if (highestRateAtEnd <= totalProjectedRate) {
                            highestRateAtEnd = totalProjectedRate
                            println("New high (all valves opened): $it, $highestRateAtEnd")
                        }
                    } else if (highestRateAtEnd < projectedMax) {
                        if (myLast.time < maxTime || eLast.time < maxTime) {
                            queue.enqueue(it)
                        }
                    }
                }
            //queue.sortQueue(compareBy<ValvePathWithElephant> { it.openedValves.size }.reversed())
        }
        return highestRateAtEnd
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