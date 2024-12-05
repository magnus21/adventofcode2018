package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2022.Day19.RESOURCE.*
import kotlin.time.ExperimentalTime

object Day19 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val blueprints = parseInput(FileParser.getFileRows(2022, "19.txt"))

        printResult("part 1") { part1(blueprints) }
        printResult("part 2") { part2(blueprints) }
    }


    private fun part1(blueprints: List<Blueprint>): Long? {

        blueprints.forEach { blueprint ->
            val nrGeodes = findBestPath(blueprint)
            println("blueprint: $blueprint, nrGeodes: $nrGeodes")
        }

        return null
    }

    private data class RobotsState(val resources: Map<RESOURCE, Int>, val robots: Map<RESOURCE, Int>)

    private fun findBestPath(blueprint: Blueprint): Int {

        val stateHighs = mutableMapOf<Map<RESOURCE, Int>, Int>()
        var foundGeodesHigh = 0
        val queue = Queue<List<RobotsState>>()
        queue.enqueue(listOf(RobotsState(mutableMapOf(), mutableMapOf(Pair(ORE, 1)))))
        while (queue.isNotEmpty()) {
            val startPath = queue.dequeue()!!
            val robots = startPath.last().robots
            val resources = startPath.last().resources

            val collectedResources = collect(robots)
            val possibleBuilds = RESOURCE.values().filter { canBuildRobotOf(it, resources, blueprint) }
            val updatedResources = addCollectedResources(collectedResources, resources)

            val newStates = getOptions(possibleBuilds, robots)
                .map { buildResourceType ->
                    if (buildResourceType == null) {
                        startPath.plus(RobotsState(updatedResources, robots))
                    } else {
                        val step = RobotsState(
                            spendResourcesForBuild(buildResourceType, updatedResources, blueprint),
                            robots.plus(Pair(buildResourceType, robots.getOrDefault(buildResourceType, 0) + 1))
                        )
                        startPath.plus(step)
                    }
                }

            newStates.forEach {
                val lastStep = it.last()
                val nrGeodes = lastStep.resources.getOrDefault(GEODE, 0)

                val stepLeft = 25 - it.size
                val maxProjection = nrGeodes + (stepLeft * (stepLeft - 1) / 2) - 1

                val high = stateHighs.getOrDefault(lastStep.robots, -1)
                if (nrGeodes >= high && it.size <= 25 && maxProjection > foundGeodesHigh) {
                    stateHighs[lastStep.robots] = nrGeodes
                    if (it.size == 25 && foundGeodesHigh < nrGeodes) {
                        foundGeodesHigh = nrGeodes
                    } else {
                        queue.enqueue(it)
                    }
                }
            }

            queue.sortQueue(compareBy<List<RobotsState>> { p -> p.last().resources.keys.size }.reversed())

        }
        return foundGeodesHigh
    }

    private fun getOptions(possibleBuilds: List<RESOURCE>, existingRobots: Map<RESOURCE, Int>): List<RESOURCE?> {
        if (possibleBuilds.isEmpty())
            return listOf(null)

        /*val newRobot = possibleBuilds.firstOrNull { existingRobots[it] == null }
        if (newRobot != null) {
            return listOf(newRobot)
        }*/

        return if (possibleBuilds.contains(GEODE)) listOf(GEODE)
        else possibleBuilds.plus(null)
    }

    private fun spendResourcesForBuild(
        buildResourceType: RESOURCE,
        resources: Map<RESOURCE, Int>,
        blueprint: Blueprint
    ): Map<RESOURCE, Int> {
        val newState = resources.toMutableMap()
        val costs = blueprint.robotCosts[buildResourceType]!!
        costs.forEach { newState.compute(it.key) { _, v -> v!! - it.value } }
        return newState
    }

    private fun addCollectedResources(
        collectedResources: Map<RESOURCE, Int>,
        resources: Map<RESOURCE, Int>
    ): Map<RESOURCE, Int> {
        val newState = resources.toMutableMap()
        collectedResources.forEach { newState.compute(it.key) { _, v -> if (v == null) 1 else v + it.value } }
        return newState
    }

    private fun collect(robots: Map<RESOURCE, Int>): Map<RESOURCE, Int> {
        return robots.map { robot -> Pair(robot.key, robot.value) }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.sum() }
    }

    private fun canBuildRobotOf(
        type: RESOURCE,
        resources: Map<RESOURCE, Int>,
        blueprint: Blueprint
    ): Boolean {
        val costs = blueprint.robotCosts[type]!!
        return costs.all { it.value <= resources.getOrDefault(it.key, 0) }
    }

    private fun part2(blueprints: List<Blueprint>): Long? {

        return null
    }

    private enum class RESOURCE { ORE, CLAY, OBSIDIAN, GEODE }
    private data class Blueprint(val id: Int, val robotCosts: Map<RESOURCE, Map<RESOURCE, Int>>)

    private fun parseInput(rows: List<String>): List<Blueprint> {
        return rows.map { row ->
            val mainParts = row.split(":")
            val id = mainParts[0].last().digitToInt()
            val robotCostStrings = mainParts[1]
                .split(".")
                .map { it.trim() }

            val oreRobotCost =
                "Each ore robot costs (\\d+) ore".toRegex().matchEntire(robotCostStrings[0])!!.groups[1]!!.value.toInt()
            val clayRobotCost =
                "Each clay robot costs (\\d+) ore".toRegex()
                    .matchEntire(robotCostStrings[1])!!.groups[1]!!.value.toInt()
            val obsidianRobotCosts = "Each obsidian robot costs (\\d+) ore and (\\d+) clay".toRegex()
                .matchEntire(robotCostStrings[2])!!.groups.drop(1).map { it!!.value.toInt() }
            val geodeRobotCosts = "Each geode robot costs (\\d+) ore and (\\d+) obsidian".toRegex()
                .matchEntire(robotCostStrings[3])!!.groups.drop(1).map { it!!.value.toInt() }

            Blueprint(
                id,
                mapOf(
                    Pair(ORE, mapOf(Pair(ORE, oreRobotCost))),
                    Pair(CLAY, mapOf(Pair(ORE, clayRobotCost))),
                    Pair(OBSIDIAN, mapOf(Pair(ORE, obsidianRobotCosts[0]), Pair(CLAY, obsidianRobotCosts[1]))),
                    Pair(GEODE, mapOf(Pair(ORE, geodeRobotCosts[0]), Pair(OBSIDIAN, geodeRobotCosts[1])))
                )
            )
        }
    }
}