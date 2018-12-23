package adventofcode

import adventofcode.Door.*
import adventofcode.util.Queue
import java.io.File

fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/20-test.txt").readLines()

    val pattern = Day20.parseInput(rawInput)

    val map = mutableMapOf<Position, Room>()
    val startPosition = Position(0, 0)
    val startRoom = Room(startPosition, mutableSetOf())
    map.put(startPosition, startRoom)

    val pathQueue = Queue<Pair<String, Room>>()
    pathQueue.enqueue(Pair(pattern, startRoom))

    while (!pathQueue.isEmpty()) {
        Day20.explorePath(pathQueue, map)

        Day20.printMap(map)
    }

    println(pattern)
    //println(map)

    Day20.printMap(map)
}

enum class Door(val code: Char, val dx: Int, val dy: Int) {
    NORTH('N', 0, -1),
    SOUTH('S', 0, 1),
    WEST('W', -1, 0),
    EAST('E', 1, 0)
}

data class Position(val x: Int, val y: Int) {
    fun walkThrough(door: Door): Position {
        return Position(this.x + door.dx, this.y + door.dy)
    }
}

data class Room(val position: Position, val doors: MutableSet<Door>)

object Day20 {

    fun explorePath(
        pathQueue: Queue<Pair<String, Room>>,
        map: MutableMap<Position, Room>
    ) {
        val path = pathQueue.dequeue()!!
        val pattern = path.first
        var currentRoom = path.second

        for (patternPos in 0 until pattern.length) {
            val code = pattern[patternPos]

            if (code == '(') {
                // Find matching parenthesis.
                val endPos = findMatchingParenthesis(pattern, patternPos)
                val endPath = pattern.drop(endPos + 1)
                val branchPath = pattern.drop(patternPos + 1).take(endPos - patternPos - 1)
                val subPaths = getSubPaths(branchPath)

                subPaths.forEach { pathQueue.enqueue(Pair(it + endPath, currentRoom)) }
                break
            } else {
                val door = values().firstOrNull { it.code == code }
                if (door != null) {
                    currentRoom.doors.add(door)

                    // Next room
                    currentRoom =
                            map.computeIfAbsent(currentRoom.position.walkThrough(door)) { key ->
                                Room(
                                    key,
                                    mutableSetOf()
                                )
                            }
                    currentRoom.doors.add(door.getCompliment())
                }
            }
        }
    }

    private fun getSubPaths(branchPath: String): List<String> {
        var depth = 0
        val subPaths = mutableListOf<String>()
        var path = ""
        for(index in 0 until branchPath.length) {
            val code = branchPath[index]
            if (code == '(') {
                depth++
            } else if (code == ')') {
                depth--
            } else if (code == '|' && depth == 0) {
                subPaths.add(path)
                path = ""
                continue
            }

            path += code
        }

        if(path.isNotEmpty()){
            subPaths.add(path)
        }

        return subPaths
    }

    private fun findMatchingParenthesis(pattern: String, patternPos: Int): Int {
        var depth = 0
        for (index in patternPos until pattern.length) {
            val code = pattern[index]
            if (code == '(') {
                depth++
            } else if (code == ')') {
                depth--
            }

            if (depth == 0) {
                return index
            }
        }
        return -1
    }

    fun parseInput(rawInput: List<String>): String {
        return rawInput[0].drop(1).dropLast(1)
    }

    private fun parseInstruction(input: String): NamedInstruction {
        val list = input.split(" ").toList()
        return NamedInstruction(
            list[0],
            Integer.valueOf(list[1]).toLong(),
            Integer.valueOf(list[2]).toLong(),
            Integer.valueOf(list[3]).toLong()
        )
    }

    fun printMap(map: MutableMap<Position, Room>) {
        val minY = map.keys.minBy { it.y }!!.y
        val maxY = map.keys.maxBy { it.y }!!.y
        val minX = map.keys.minBy { it.x }!!.x
        val maxX = map.keys.maxBy { it.x }!!.x

        println()
        for (y in minY..maxY) {
            val rows = mutableListOf("", "", "")
            for (x in minX..maxX) {
                val room = map.get(Position(x, y))
                if (room != null) {
                    val doors = room.doors

                    if(x == minX) {
                        rows[0] += if (doors.contains(NORTH)) "#-#" else "###"
                        rows[1] += if (doors.contains(WEST)) "|" else "#"
                        rows[1] += if (room.position.x == 0 && room.position.y == 0) "X" else "."
                        rows[1] += if (doors.contains(EAST)) "|" else "#"
                        rows[2] += if (doors.contains(SOUTH)) "#-#" else "###"
                    } else {
                        rows[0] += if (doors.contains(NORTH)) "-#" else "##"
                        rows[1] += if (room.position.x == 0 && room.position.y == 0) "X" else "."
                        rows[1] += if (doors.contains(EAST)) "|" else "#"
                        rows[2] += if (doors.contains(SOUTH)) "-#" else "##"
                    }
                } else {
                    if(x == minX) {
                        rows[0] += "   "
                        rows[1] += "   "
                        rows[2] += "   "
                    } else {
                        rows[0] += "  "
                        rows[1] += "  "
                        rows[2] += "  "
                    }
                }
            }

            if(y == minY) {
                rows.forEach { println(it) }
            } else {
                println(rows[1])
                println(rows[2])
            }
        }
        println()
    }
}

private fun Door.getCompliment(): Door {
    return when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        WEST -> EAST
        else -> WEST
    }
}
