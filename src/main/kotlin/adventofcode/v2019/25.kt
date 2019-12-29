package adventofcode.v2019

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.random.Random
import kotlin.system.measureTimeMillis

object Day25 {

    private val badItems = listOf("infinite loop", "escape pod", "giant electromagnet", "photons", "molten lava")

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "25.txt").map(String::toLong)

        // sswnn to check point

        // Run program.
        val time1 = measureTimeMillis {
            //val scanner = Scanner(System.`in`)

            val computer = IntCodeComputer(input.toMutableList())
            val freshComputer = IntCodeComputer(
                computer.program.toMutableList(),
                computer.instructionPointer,
                computer.relativeBase
            )

            val programInput = mutableListOf<Long>()


            val rooms = mutableListOf<Room>()
            exploreRooms(computer, rooms, programInput)

            val validItems = getItems(rooms).filter { !badItems.contains(it) }

            val items = mutableSetOf<String>()
            pickUpAllItemsAndGotoCheckPoint(freshComputer, mutableListOf(), validItems.toMutableSet(), items)

            val result = freshComputer.runWithInput(toAscii("inv"))
            result.first.forEach { print(it.toChar()) }

            val invCombinations = (1..items.size).flatMap { AdventOfCodeUtil.combinations(items, it, setOf()) }
            for (i in 0 until invCombinations.size) {
                val combination = invCombinations[i]

                // drop all and test combination -> go north
                items.forEach { freshComputer.runWithInput(toAscii("drop $it")) }
                combination.forEach { freshComputer.runWithInput(toAscii("take $it")) }

                val testResult = freshComputer.runWithInput(toAscii("north"))

                if (testResult.second == -1) {
                    println("Correct items: $combination")
                    testResult.first.forEach { print(it.toChar()) }
                    break
                }
            }
        }
        println("Time part 1: ($time1 milliseconds)")
    }

    private fun getItems(rooms: MutableList<Room>) =
        rooms.filter { it.items.size > 0 }.flatMap { it.items }.distinct().toSet()

    private fun pickUpAllItemsAndGotoCheckPoint(
        computer: IntCodeComputer,
        programInput: List<Long>,
        validItems: MutableSet<String>,
        items: MutableSet<String>
    ) {
        val result = computer.runWithInput(programInput)

        val room = parseRoom(result.first)

        if (items == validItems && room.name == "== Security Checkpoint ==") {
            println(room)
            return
        }

        room.items
            .filter { !badItems.contains(it) }
            .forEach {
                computer.runWithInput(toAscii("take $it"))
                items.add(it)
            }

        pickUpAllItemsAndGotoCheckPoint(
            computer,
            toAscii(room.doors[Random.nextInt(room.doors.size)].name.toLowerCase()),
            validItems,
            items
        )
    }

    private fun exploreRooms(
        computer: IntCodeComputer,
        rooms: MutableList<Room>,
        programInput: List<Long>
    ) {
        val result = computer.runWithInput(programInput)

        val room = parseRoom(result.first)
        var skipRoom = false
        room.items
            .filter { !badItems.contains(it) }
            .forEach {
                val takeResult = computer.runWithInput(toAscii("take $it"))
                if (takeResult.second == IntCodeComputer.DONE) {
                    skipRoom = true
                }
            }

        if (!skipRoom && !rooms.contains(room)) {

            rooms.add(room)

            room.doors.forEach {
                exploreRooms(
                    IntCodeComputer(
                        computer.program.toMutableList(),
                        computer.instructionPointer,
                        computer.relativeBase
                    ),
                    rooms,
                    toAscii(it.name.toLowerCase())
                )
            }
        }
    }

    private fun parseRoom(output: MutableList<Long>): Room {
        val outputRows = mutableListOf<String>()
        var row = ""
        for (i in 0 until output.size) {
            val value = output[i]
            if (value == 10L) {
                outputRows.add(row)
                row = ""
            } else {
                row += value.toChar()
            }
        }
        val name = if (outputRows.size > 3) outputRows[3] else ""
        val description = if (outputRows.size > 4) outputRows[4] else ""

        val doors = mutableListOf<Direction>()
        val items = mutableListOf<String>()
        var doorSection = false
        var itemsSection = false
        for (i in 0 until outputRows.size) {
            val value = outputRows[i]

            if (value == "") {
                doorSection = false
                itemsSection = false
            }

            if (doorSection) {
                doors.add(Direction.valueOf(value.takeLast(5).trim().toUpperCase()))
            } else if (itemsSection) {
                items.add(value.substring(1).trim())
            }


            if (value == "Doors here lead:") {
                doorSection = true
            } else if (value == "Items here:") {
                itemsSection = true
            }
        }
        return Room(name, description, doors, items)
    }

    private fun toAscii(code: String): List<Long> {
        return code.toCharArray().map(Char::toLong).plusElement(10L)
    }

    private data class Room(
        val name: String,
        val description: String,
        val doors: MutableList<Direction>,
        val items: MutableList<String>
    ) {
        override fun toString(): String {
            return "$name\n\n$description\n\nDoors:\n$doors\n\nItems:\n$items"
        }
    }

    enum class Direction(val code: Int) {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4)
    }

}