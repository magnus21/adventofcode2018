package adventofcode.v2021

import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.system.measureTimeMillis

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {
        val caveMap = parseInput(FileParser.getFileRows(2021, "12.txt"))

        val time1 = measureTimeMillis {
            val visitedPredicate =
                { connectedCave: String, cavePath: CavePath -> !cavePath.visitedSmallCaves.contains(connectedCave) }

            val distinctCavePaths = findPaths(caveMap, visitedPredicate)

            println("answer part 1: ${distinctCavePaths.size}")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val visitedPredicate = { connectedCave: String, cavePath: CavePath ->
                !cavePath.visitedSmallCaves.contains(connectedCave) ||
                        (connectedCave != "start" && !cavePath.visitedSmallCaves.values.any { it == 2 })
            }

            val distinctCavePaths = findPaths(caveMap, visitedPredicate)

            println("answer part 2: ${distinctCavePaths.size}")
        }
        println("Time: $time2 ms")
    }

    private fun findPaths(
        caveMap: MutableMap<String, Cave>,
        visitedPredicate: (String, CavePath) -> Boolean
    ): MutableSet<CavePath> {
        val queue = Queue<CavePath>()
        queue.enqueue(CavePath())

        val distinctCavePaths = mutableSetOf<CavePath>()
        while (queue.isNotEmpty()) {
            explorePath(distinctCavePaths, queue, caveMap, visitedPredicate)
        }
        return distinctCavePaths
    }

    private fun explorePath(
        distinctCavePaths: MutableSet<CavePath>,
        queue: Queue<CavePath>,
        caveMap: MutableMap<String, Cave>,
        visitedPredicate: (String, CavePath) -> Boolean
    ) {
        val cavePath = queue.dequeue()!!

        caveMap[cavePath.path.last()]!!.connectedCaves
            .filter { connectedCave -> visitedPredicate.invoke(connectedCave, cavePath) }
            .forEach { connectedCave ->
                val newPath = CavePath(
                    cavePath.path.plus(connectedCave).toMutableList(),
                    if (caveMap[connectedCave]!!.small) cavePath.visitedSmallCaves
                        .plus(Pair(connectedCave, cavePath.visitedSmallCaves.getOrDefault(connectedCave, 0) + 1))
                        .toMutableMap()
                    else cavePath.visitedSmallCaves.toMutableMap()
                )

                if (connectedCave == "end") {
                    distinctCavePaths.add(newPath)
                } else {
                    queue.enqueue(newPath)
                }
            }
    }

    private fun parseInput(rows: List<String>): MutableMap<String, Cave> {
        val caveMap = mutableMapOf<String, Cave>()
        rows.forEach { row ->
            val caveNames = row.split("-")
            caveNames.forEach { cave ->
                caveMap.computeIfAbsent(cave) { name ->
                    Cave(name, name.all { it.isLowerCase() }, caveNames.minus(name).toMutableSet())
                }.connectedCaves.addAll(caveNames.minus(cave))
            }
        }
        return caveMap
    }

    data class Cave(val name: String, val small: Boolean, val connectedCaves: MutableSet<String>)
    data class CavePath(
        val path: MutableList<String> = mutableListOf("start"),
        val visitedSmallCaves: MutableMap<String, Int> = mutableMapOf(Pair("start", 1))
    )
}