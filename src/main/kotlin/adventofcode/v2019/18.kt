package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2019.Day18.Direction.*
import kotlin.system.measureTimeMillis

object Day18 {


    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "18.txt")
        val (size, map) = parseField(input)

        val startPosition = map.entries.find { it.value == '@' }!!.key

        val keys = map.filterValues { it != '#' && it != '.' && it.isLowerCase() }.map { it.value }
        val nrOfKeys = keys.size

        println("Keys: $keys")

        printMap(map, startPosition, size)

        val time1 = measureTimeMillis {

            val progressMap = mutableMapOf<Pair<Position, Set<Char>>, Int>()
            val keysProgressMap = mutableMapOf<Set<Char>, Int>()

            val queue = Queue<Path>()
            queue.enqueue(Path(startPosition, mutableSetOf(), mutableListOf(), mutableSetOf()))

            var c=0
            while (!queue.isEmpty()) {
                val path = queue.dequeue()!!

                if (path.keys.size == nrOfKeys) {
                    // Too high: 3052
                    // Result part 1: 3048 Keys: [u, o, x, n, e, q, f, b, i, w, c, k, m, t, y, s, d, z, r, g, a, j, h, l, p, v]
                    println("Result part 1: ${path.entireTrail.size} Keys: ${path.keys}")
                    break
                }

                processPath(path, map, queue, progressMap, keysProgressMap)
                c++
                if(c % 100000 == 0) {
                    println("Queue size: ${queue.items.size}, keys: ${path.keys}")
                }
            }
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {


        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun processPath(
        path: Path,
        map: MutableMap<Position, Char>,
        queue: Queue<Path>,
        progressMap: MutableMap<Pair<Position, Set<Char>>, Int>,
        keysProgressMap: MutableMap<Set<Char>, Int>
    ) {

        // Random limit that seems to work :/
        if(path.trailFromLastKey.size > 500) {
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
                        path.keys.plus(it.second).toMutableSet()
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
        neighbours.filter { it.second != '.' && it.second != '@' }
            .filter { it.second.isLowerCase() }
            .filter { !path.trailFromLastKey.contains(it.first) }
            .filter {
                (path.entireTrail.size + path.trailFromLastKey.size) < progressMap.getOrDefault(
                    Pair(
                        it.first,
                        path.keys.plus(it.second).toMutableSet()
                    ), 100000000
                )
            }
            //.filter { path.entireTrail.size + path.trailFromLastKey.size <= keysProgressMap.getOrDefault(path.keys.plus(it.second).toMutableSet(),100000000) }
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
                //keysProgressMap[path.keys.plus(it.second).toMutableSet()] = path.entireTrail.size + path.trailFromLastKey.size
                queue.enqueue(newPath)
            }

        // Doors
        neighbours.filter { it.second != '.' && it.second != '@' }
            .filter { it.second.isUpperCase() }
            .filter {
                (path.entireTrail.size + path.trailFromLastKey.size) < progressMap.getOrDefault(
                    Pair(
                        it.first,
                        path.keys.plus(it.second).toMutableSet()
                    ), 100000000
                )
            }
            .filter { !path.trailFromLastKey.contains(it.first) }
            .filter { path.keys.contains(it.second.toLowerCase()) }
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

    enum class Direction(val code: Int) {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4)
    }
}