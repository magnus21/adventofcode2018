package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2019.Day18.Direction.*
import kotlin.system.measureTimeMillis

object Day18 {


    @JvmStatic
    fun main(args: Array<String>) {

        val time1 = measureTimeMillis {
            /* val input = FileParser.getFileRows(2019, "18.txt")
             val (size, map) = parseField(input)

             val startPosition = map.entries.find { it.value == '@' }!!.key

             val keys = map.filterValues { it.isLowerCase() }.map { it.value }
             val nrOfKeys = keys.size

             println("Keys: $keys")

             printMap(map, startPosition, size)

             val progressMap = mutableMapOf<Pair<Position, Set<Char>>, Int>()

             val queue = Queue<Path>()
             queue.enqueue(Path(startPosition, mutableSetOf(), mutableListOf(), mutableSetOf()))

             var c = 0
             while (!queue.isEmpty()) {
                 val path = queue.dequeue()!!

                 if (path.keys.size == nrOfKeys) {
                     // Takes a few minutes :/
                     // Result part 1: 3048 Keys: [u, o, x, n, e, q, f, b, i, w, c, k, m, t, y, s, d, z, r, g, a, j, h, l, p, v]
                     println("Result part 1: ${path.entireTrail.size} Keys: ${path.keys}")
                     break
                 }

                 processPath(path, map, queue, progressMap)
                 c++
                 if (c % 100000 == 0) {
                     println("Queue size: ${queue.items.size}, keys: ${path.keys}")
                 }
             }*/
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            val input = FileParser.getFileRows(2019, "18.2.txt")
            val (size, map) = parseField(input)

            val startPositions = map.entries.filter { it.value == '@' }.map { it.key }.toMutableList()
            val keysAndDoors = getKeysAndDoorsPerQuadrant(map, size)

            val progressMap = mutableMapOf<Pair<Position, Set<Char>>, Int>()

            val result = (0..3).map { processQuadrant(it, startPositions, keysAndDoors, map, progressMap) }.sum()

            println("Answer part 2: $result")

        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun processQuadrant(
        quadrant: Int,
        startPositions: MutableList<Position>,
        keysAndDoors: Map<Int, Pair<List<Char>, List<Char>>>,
        map: MutableMap<Position, Char>,
        progressMap: MutableMap<Pair<Position, Set<Char>>, Int>
    ): Int {
        val queue = Queue<Path>()
        queue.enqueue(Path(startPositions[quadrant], mutableSetOf(), mutableListOf(), mutableSetOf()))

        val nrOfKeys = keysAndDoors[quadrant]!!.first.size
        while (!queue.isEmpty()) {
            val path = queue.dequeue()!!

            if (path.keys.size == nrOfKeys) {
                println("Result part 2($quadrant): ${path.entireTrail.size} Keys: ${path.keys}")
                return path.entireTrail.size
            }

            processPath(path, map, queue, progressMap, keysAndDoors[quadrant]!!, true)
        }
        return 0
    }

    private fun getKeysAndDoorsPerQuadrant(
        map: MutableMap<Position, Char>,
        size: Pair<Int, Int>
    ): Map<Int, Pair<List<Char>, List<Char>>> {

        val keysAndDoors = mutableMapOf<Int, Pair<List<Char>, List<Char>>>()

        keysAndDoors[0] = Pair(
            map.filter { it.key.x <= size.first / 2 && it.key.y <= size.second / 2 }.filter { it.value.isLowerCase() }.map { it.value },
            map.filter { it.key.x <= size.first / 2 && it.key.y <= size.second / 2 }.filter { it.value.isUpperCase() }.map { it.value }
        )

        keysAndDoors[1] = Pair(
            map.filter { it.key.x >= size.first / 2 && it.key.y <= size.second / 2 }.filter { it.value.isLowerCase() }.map { it.value },
            map.filter { it.key.x >= size.first / 2 && it.key.y <= size.second / 2 }.filter { it.value.isUpperCase() }.map { it.value }
        )

        keysAndDoors[2] = Pair(
            map.filter { it.key.x <= size.first / 2 && it.key.y >= size.second / 2 }.filter { it.value.isLowerCase() }.map { it.value },
            map.filter { it.key.x <= size.first / 2 && it.key.y >= size.second / 2 }.filter { it.value.isUpperCase() }.map { it.value }
        )

        keysAndDoors[3] = Pair(
            map.filter { it.key.x >= size.first / 2 && it.key.y >= size.second / 2 }.filter { it.value.isLowerCase() }.map { it.value },
            map.filter { it.key.x >= size.first / 2 && it.key.y >= size.second / 2 }.filter { it.value.isUpperCase() }.map { it.value }
        )

        return keysAndDoors
    }

    private fun processPath(
        path: Path,
        map: MutableMap<Position, Char>,
        queue: Queue<Path>,
        progressMap: MutableMap<Pair<Position, Set<Char>>, Int>,
        keysAndDoors: Pair<List<Char>, List<Char>> = Pair(mutableListOf(), mutableListOf()),
        ignoreOtherQuadrantDoors: Boolean = false
    ) {

        // Random limit that seems to work :/
        if (path.trailFromLastKey.size > 500) {
            return
        }

        val neighbours = getNeighbours(path.position, map)

        // Dots
        neighbours.filter { it.second == '.' || it.second == '@' }
            .filter { !path.trailFromLastKey.contains(it.first) }
            .filter {
                (path.entireTrail.size + path.trailFromLastKey.size) < progressMap.getOrDefault(
                    Pair(
                        it.first,
                        path.keys.toMutableSet()
                    ), 100000000
                )
            }
            .forEach {
                val newPath = Path(
                    it.first,
                    path.trailFromLastKey.plus(path.position).toMutableSet(),
                    path.entireTrail,
                    path.keys
                )
                queue.enqueue(newPath)
            }

        // Keys
        neighbours.filter { it.second.isLowerCase() }
            .filter { !path.trailFromLastKey.contains(it.first) }
            .filter {
                (path.entireTrail.size + path.trailFromLastKey.size) < progressMap.getOrDefault(
                    Pair(
                        it.first,
                        path.keys.plus(it.second).toMutableSet()
                    ), 100000000
                )
            }
            .forEach {

                val trail =
                    if (path.keys.contains(it.second)) path.trailFromLastKey.plus(path.position).toMutableSet()
                    else mutableSetOf()
                val entireTrail =
                    if (path.keys.contains(it.second)) path.entireTrail
                    else path.entireTrail.plus(path.trailFromLastKey.plus(path.position)).toMutableList()

                val newPath = Path(
                    it.first,
                    trail,
                    entireTrail,
                    path.keys.plus(it.second).toMutableSet()
                )

                progressMap[Pair(it.first, path.keys.plus(it.second).toMutableSet())] = path.entireTrail.size +
                        path.trailFromLastKey.size

                queue.enqueue(newPath)
            }

        // Doors
        neighbours.filter { it.second.isUpperCase() }
            .filter {
                (path.entireTrail.size + path.trailFromLastKey.size) < progressMap.getOrDefault(
                    Pair(
                        it.first,
                        path.keys.toMutableSet()
                    ), 100000000
                )
            }
            .filter { !path.trailFromLastKey.contains(it.first) }
            .filter {
                path.keys.contains(it.second.toLowerCase()) || (ignoreOtherQuadrantDoors && !keysAndDoors.first.contains(
                    it.second.toLowerCase()
                ))
            }
            .forEach {
                val newPath = Path(
                    it.first,
                    path.trailFromLastKey.plus(path.position).toMutableSet(),
                    path.entireTrail,
                    path.keys
                )
                queue.enqueue(newPath)
            }
    }

    private fun getNeighbours(position: Position, map: MutableMap<Position, Char>): List<Pair<Position, Char>> {
        return Direction.values()
            .map { getPosition(position, it) }
            .filter { map[it] != '#' }
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


    private fun parseField(input: List<String>): Pair<Pair<Int, Int>, MutableMap<Position, Char>> {

        val fieldSize = Pair(input[0].length, input.size)
        val fieldPoints = mutableMapOf<Position, Char>()

        for (y in 0 until fieldSize.second) {
            val row = input[y]
            for (x in 0 until fieldSize.first) {
                fieldPoints[Position(x, y)] = row[x]
            }
        }

        return Pair(fieldSize, fieldPoints)
    }


    private fun printMap(
        field: MutableMap<Position, Char>,
        pathPosition: Position,
        size: Pair<Int, Int>
    ) {
        for (y in 0 until size.second) {
            for (x in 0 until size.first) {
                val tile = field[Position(x, y)]
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
        val trailFromLastKey: MutableSet<Position>,
        val entireTrail: MutableList<Position>,
        val keys: MutableSet<Char>
    )

    private data class Path2(
        val positions: MutableList<Position>,
        val keys: MutableList<Char>,
        val trailLength: Int,
        val trailFromLastKey: MutableList<MutableList<Position>>,
        var currentRobot: Int = 0,
        var shiftCount: Int = 0
    )

    private data class QPath(
        val position: Position,
        val trailFromLastKey: MutableSet<Position>,
        val entireTrail: MutableList<Position>,
        val keys: MutableSet<Char>
    )

    enum class Direction(val code: Int) {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4)
    }
}