package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2019.Day20.Direction.*
import kotlin.system.measureTimeMillis

object Day20 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "20.txt")
        val (size, map, portals) = parseMap(input)

        val startPosition =
            map.entries.find { it.key == portals.entries.filter { portal -> portal.value.code == "AA" }[0].key }!!.key

        println("Portals: $portals")
        printMap(map, startPosition, size)

        val time1 = measureTimeMillis {
            val queue = Queue<Path>()
            queue.enqueue(Path(startPosition, mutableSetOf(), mutableListOf(), mutableListOf()))

            while (!queue.isEmpty()) {
                val path = queue.dequeue()!!

                if (portals.getOrDefault(path.position, Portal("?", false)).code == "ZZ") {
                    println("Result part 1: ${path.trail.size}, trail: ${path.trail}")
                    break
                }

                processPath(path, map, queue, portals)
            }
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            val queue = Queue<Path>()
            queue.enqueue(Path(startPosition, mutableSetOf(), mutableListOf(), mutableListOf()))

            while (!queue.isEmpty()) {
                val path = queue.dequeue()!!

                if (portals.getOrDefault(path.position, Portal("?", false)).code == "ZZ" && path.level == 0) {
                    println("Result part 2: ${path.entireTrail.size + path.trail.size}")
                    path.portals.forEach(::println)
                    break
                }

                processPathWithLevels(path, map, queue, portals)
            }

        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun processPathWithLevels(
        path: Path,
        map: MutableMap<Position, Char>,
        queue: Queue<Path>,
        portals: MutableMap<Position, Portal>
    ) {
        val dots = getNeighbours(path.position, map)
            .filter { it.second == '.' }
            .filter { !path.trail.contains(it.first) }

        if (dots.isNotEmpty()) {
            dots.forEach {
                queue.enqueue(
                    Path(
                        it.first,
                        path.trail.plus(path.position).toMutableSet(),
                        path.entireTrail,
                        path.portals,
                        path.level
                    )
                )
            }
        } else if (portals.containsKey(path.position) && portalOpen(path, portals[path.position]!!)) {
            val portal = portals[path.position]!!
            val portalEnds =
                portals.filter { it.value.code == portal.code }.filter { it.key != path.position }.map { it.key }

            val level = if (portal.isInner) path.level + 1 else path.level - 1
            queue.enqueue(
                Path(
                    portalEnds[0],
                    mutableSetOf(),
                    path.entireTrail.plus(path.trail.plus(path.position)).toMutableList(),
                    path.portals.plus(Triple(portal.code,path.trail.size,level)).toMutableList(),
                    level
                )
            )
        }
    }

    private fun portalOpen(path: Path, portal: Portal): Boolean {
        if (!portal.isInner) {
            if (path.level == 0 && portal.code != "ZZ") {
                return false
            } else if (path.level > 0 && (portal.code == "AA" || portal.code == "ZZ")) {
                return false
            }
        }
        return true
    }

    private fun processPath(
        path: Path,
        map: MutableMap<Position, Char>,
        queue: Queue<Path>,
        portals: MutableMap<Position, Portal>
    ) {
        val dots = getNeighbours(path.position, map)
            .filter { it.second == '.' }
            .filter { !path.trail.contains(it.first) }

        if (dots.isNotEmpty()) {
            dots.forEach {
                queue.enqueue(
                    Path(
                        it.first, path.trail.plus(path.position).toMutableSet(), mutableListOf(),
                        mutableListOf()
                    )
                )
            }
        } else if (portals.containsKey(path.position) && portals[path.position]!!.code != "AA") {
            val portal = portals[path.position]!!
            val portalEnds =
                portals.filter { it.value.code == portal.code }.filter { it.key != path.position }.map { it.key }

            queue.enqueue(
                Path(
                    portalEnds[0], path.trail.plus(path.position).toMutableSet(), mutableListOf(),
                    mutableListOf()
                )
            )
        }
    }

    private fun getNeighbours(position: Position, map: MutableMap<Position, Char>): List<Pair<Position, Char>> {
        return values()
            .map { getPosition(position, it) }
            .filter { map[it] != '#' && map[it] != null }
            .map { Pair(it, map[it]!!) }
    }

    private fun getPosition(
        startPosition: Position,
        direction: Direction
    ): Position {
        return when (direction) {
            NORTH -> Position(startPosition.x, startPosition.y - 1)
            SOUTH -> Position(startPosition.x, startPosition.y + 1)
            WEST -> Position(startPosition.x - 1, startPosition.y)
            else -> Position(startPosition.x + 1, startPosition.y)
        }
    }


    private fun parseMap(input: List<String>): Triple<Pair<Int, Int>, MutableMap<Position, Char>, MutableMap<Position, Portal>> {

        val fieldSize = Pair(input.maxOf { it.length }, input.size)
        val map = mutableMapOf<Position, Char>()

        for (y in 0 until fieldSize.second) {
            val row = input[y]
            for (x in 0 until fieldSize.first) {
                val value: Char = if (row.length <= x) ' ' else row[x]
                map[Position(x, y)] = value
            }
        }

        val portals = mutableMapOf<Position, Portal>()
        map.entries
            .filter { it.value.isLetter() }
            .forEach { letter ->
                val neighbours = getNeighbours(letter.key, map)
                val dotNeighbours = neighbours.filter { it.second == '.' }
                if (dotNeighbours.size == 1) {
                    val letterNeighbours = neighbours.filter { it.second.isLetter() }
                    if (letterNeighbours.size == 1) {
                        val pos = dotNeighbours[0].first
                        val isOuter =
                            pos.x == 2 || pos.x == fieldSize.first - 3 || pos.y == 2 || pos.y == fieldSize.second - 3

                        portals[pos] = Portal(
                            listOf(Pair(letter.key, letter.value), letterNeighbours[0])
                                .sortedWith(compareBy<Pair<Position, Char>> { it.first.x }.thenBy { it.first.y })
                                .map { it.second }
                                .joinToString(""),
                            !isOuter
                        )
                    }
                }
            }

        return Triple(fieldSize, map, portals)
    }


    private fun printMap(
        map: MutableMap<Position, Char>,
        pathPosition: Position,
        size: Pair<Int, Int>
    ) {
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                val tile = map[Position(x, y)]
                if (Position(x, y) == pathPosition) {
                    print("*")
                } else {
                    print(tile)
                }
            }
            println()
        }
        println()
    }

    private data class Position(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }

    private data class Path(
        val position: Position,
        val trail: MutableSet<Position>,
        val entireTrail: MutableList<Position>,
        val portals: MutableList<Triple<String, Int, Int>>,
        val level: Int = 0
    )

    private data class Portal(
        val code: String,
        val isInner: Boolean
    )

    enum class Direction(val code: Int) {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4)
    }
}