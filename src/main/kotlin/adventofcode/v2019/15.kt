package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import adventofcode.v2019.Day15.Direction.*
import adventofcode.v2019.Day15.Tile.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object Day15 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "15.txt").map { it.toLong() }

        val time1 = measureTimeMillis {

            // Part 1.1: Map out maze (take a while to run..).
            /*val program = input.toMutableList()
            val computer = IntCodeComputer(program)

            val droidPosition = Position(0, 0)
            val field = mutableMapOf<Position, Tile>()
            field[Position(0, 0)] = VISITED
            var inputDirection = NORTH
            var oxygenFound = false
            while (true) {
                val newPosition = getPosition(droidPosition, inputDirection)
                val result = computer.runWithInput(listOf(inputDirection.code.toLong())).first[0].toInt()

                if (result != WALL.code) {
                    droidPosition.x = newPosition.x
                    droidPosition.y = newPosition.y
                }

                updateField(field, result, newPosition)

                val size = getFieldSize(field, droidPosition)

                if (oxygenFound && fieldFullyMappedOut(field, size)) {
                    println("Done!!!")
                    break
                }

                if (result == OXYGEN_SYSTEM.code) {
                    printField(field, droidPosition, size)
                    println(newPosition)
                    oxygenFound = true
                }

                inputDirection = updateDirection(inputDirection, result, field, droidPosition)
            }
            printField(field, droidPosition, getFieldSize(field, droidPosition))
*/

            // Part 1.2: Find shortest path to oxygen.
            val maze = FileParser.getFileRows(2019, "15.1.txt")
            val map = parseMaze(maze)

            // Start position
            val droidPosition = Position(21, 21)
            val size = getFieldSize(map, droidPosition)
            printField(map, droidPosition, size)

            val queue = Queue<Path>()
            queue.enqueue(Path(droidPosition, mutableSetOf()))

            while (!queue.isEmpty()) {
                val path = queue.dequeue()!!

                if (map[path.position] == OXYGEN_SYSTEM) {
                    println("Shortest path: ${path.trail.size}")
                    //println(path.trail)
                    break
                }
                getPossibleSteps(path, map).forEach {
                    queue.enqueue(Path(it, path.trail.plus(path.position).toMutableSet()))
                }
            }
        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            val maze = FileParser.getFileRows(2019, "15.1.txt")
            val map = parseMaze(maze)

            var count = 0
            while(true) {
                spreadOxygen(map)
                count++

                if(map.keys.none{map[it] == VISITED}) {
                    println("Part two results (minutes): ${count}")
                    break
                }
            }

        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun spreadOxygen(map: MutableMap<Position, Tile>) {
        val dotNeighbours = map.keys.filter { map[it] == OXYGEN_SYSTEM }
            .flatMap { position ->
                Direction.values()
                    .map { getPosition(position, it) }
                    .filter { map[it] == VISITED }
            }.toSet()

        dotNeighbours.forEach {
            map[it] = OXYGEN_SYSTEM
        }
    }

    private fun getPossibleSteps(
        path: Path,
        map: MutableMap<Position, Tile>
    ): List<Position> {
        return Direction.values()
            .map { getPosition(path.position, it) }
            .map { Pair(it, map[it]) }
            .filter { it.second!! == VISITED || it.second!! == OXYGEN_SYSTEM }
            .filter { !path.trail.contains(it.first) }
            .map { it.first }
    }

    private fun parseMaze(maze: List<String>): MutableMap<Position, Tile> {
        // start 21,22
        val tiles = maze.map { row ->
            row.toCharArray().map {
                when (it) {
                    WALL.display -> WALL
                    VISITED.display -> VISITED
                    OXYGEN_SYSTEM.display -> OXYGEN_SYSTEM
                    else -> WALL
                }
            }
        }

        val field = mutableMapOf<Position, Tile>()
        for (y in 0 until tiles.size) {
            for (x in 0 until tiles[0].size) {
                field[Position(x, y)] = tiles[y][x]
            }
        }
        return field
    }

    private fun fieldFullyMappedOut(
        field: MutableMap<Position, Tile>,
        size: Pair<Pair<Int, Int>, Pair<Int, Int>>
    ): Boolean {
        return field.keys
            .filter { it.x > size.first.first && it.x < size.first.second }
            .filter { it.y > size.second.first && it.y < size.second.second }
            .count() == (size.first.second - size.first.first - 1) * (size.second.second - size.second.first - 1)
    }

    private fun getFieldSize(
        field: MutableMap<Position, Tile>,
        droidPosition: Position
    ): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val fieldWithDroid = field.toMutableMap()
        fieldWithDroid[droidPosition] = REPAIR_DROID
        val xSpan = Pair(fieldWithDroid.keys.map { it.x }.min()!!, fieldWithDroid.keys.map { it.x }.max()!!)
        val ySpan = Pair(fieldWithDroid.keys.map { it.y }.min()!!, fieldWithDroid.keys.map { it.y }.max()!!)

        return Pair(xSpan, ySpan)
    }

    private fun updateDirection(
        currentDirection: Direction,
        result: Int,
        field: MutableMap<Position, Tile>,
        droidPosition: Position
    ): Direction {
        if (result != WALL.code && result != VISITED.code) {
            return currentDirection
        }

        val openDirections = getOpenDirections(field, droidPosition)
        if (openDirections.size == 1) {
            return openDirections[0].second
        }

        val unexplored = openDirections.find { field[it.first] == null }
        if (unexplored != null) {
            return unexplored.second
        }

        return openDirections[Random.nextInt(openDirections.size)].second
    }

    private fun getOpenDirections(
        field: MutableMap<Position, Tile>,
        droidPosition: Position
    ): List<Pair<Position, Direction>> {
        return Direction.values()
            .filter {
                field[getPosition(droidPosition, it)] == null || field[getPosition(
                    droidPosition,
                    it
                )] == VISITED
            }
            .map { Pair(getPosition(droidPosition, it), it) }
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

    private fun updateField(
        field: MutableMap<Position, Tile>,
        result: Int,
        newPosition: Position
    ) {
        when (result) {
            WALL.code -> field[newPosition] = WALL
            VISITED.code -> field[newPosition] = VISITED
            OXYGEN_SYSTEM.code -> field[newPosition] = OXYGEN_SYSTEM
        }
    }


    private fun printField(
        field: MutableMap<Position, Tile>,
        droidPosition: Position,
        size: Pair<Pair<Int, Int>, Pair<Int, Int>>
    ) {
        println("======================================")
        for (y in size.first.first..size.first.second) {
            for (x in size.second.first..size.second.second) {
                val tile = field[Position(x, y)]
                if (x == 0 && y == 0) {
                    print("S")
                } else if (Position(x, y) == droidPosition) {
                    print("D")
                } else {
                    when {
                        tile == null -> print(" ")
                        droidPosition == Position(x, y) -> print(REPAIR_DROID.display)
                        else -> print(tile.display)
                    }
                }
            }
            println()
        }
        println("======================================")
    }

    private data class Position(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }

    private data class Path(val position: Position, val trail: MutableSet<Position>)

    enum class Tile(val code: Int, val display: Char) {
        WALL(0, '#'),
        VISITED(1, '.'),
        OXYGEN_SYSTEM(2, 'O'),
        REPAIR_DROID(3, 'D')
    }

    enum class Direction(val code: Int) {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4)
    }
}