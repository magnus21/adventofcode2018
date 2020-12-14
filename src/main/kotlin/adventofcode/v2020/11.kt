package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day11 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "11.txt")
        val field = parseField(input)

        val fieldSize = getFieldSize(field)
        printField(field, fieldSize)

        val time1 = measureTimeMillis {

            var updatedField = field.toMutableMap().toMap()
            while (true) {
                val newField = updateSeats(updatedField, fieldSize)

                printField(newField, getFieldSize(field))
                if (updatedField == newField) {
                    println("No more changes, occupied seats: ${updatedField.values.filter { it == Tile.OCCUPIED }.count()}")
                    break
                }
                updatedField = newField
            }

        }
        println("Time part 1: ($time1 milliseconds)")


        val time2 = measureTimeMillis {
            var updatedField = field.toMutableMap().toMap()
            while (true) {
                val newField = updateSeats2(updatedField, fieldSize)

                printField(newField, getFieldSize(field))
                if (updatedField == newField) {
                    println("No more changes, occupied seats: ${updatedField.values.filter { it == Tile.OCCUPIED }.count()}")
                    break
                }
                updatedField = newField
            }
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun updateSeats(
        field: Map<Position, Tile>,
        fieldSize: Pair<Pair<Int, Int>, Pair<Int, Int>>
    ): Map<Position, Tile> {
        return field.map {
            val occupiedCount =
                getNeighbours(it.key, field, fieldSize).filter { seat -> seat.second == Tile.OCCUPIED }.count()
            Pair(
                it.key,
                when {
                    it.value == Tile.EMPTY && occupiedCount == 0 -> Tile.OCCUPIED
                    it.value == Tile.OCCUPIED && occupiedCount >= 4 -> Tile.EMPTY
                    else -> it.value
                }
            )
        }.toMap()
    }

    private fun updateSeats2(
        field: Map<Position, Tile>,
        fieldSize: Pair<Pair<Int, Int>, Pair<Int, Int>>
    ): Map<Position, Tile> {
        return field.map {
            val occupiedCount = getFirstSeatsOccupiedCount(it.key, field, fieldSize)
            Pair(
                it.key,
                when {
                    it.value == Tile.EMPTY && occupiedCount == 0 -> Tile.OCCUPIED
                    it.value == Tile.OCCUPIED && occupiedCount >= 5 -> Tile.EMPTY
                    else -> it.value
                }
            )
        }.toMap()
    }


    private fun getNeighbours(
        pos: Position,
        field: Map<Position, Tile>,
        fieldSize: Pair<Pair<Int, Int>, Pair<Int, Int>>
    ): List<Pair<Position, Tile>> {
        val directions = listOf(
            Position(-1, -1),
            Position(-1, 0),
            Position(-1, 1),
            Position(1, -1),
            Position(1, 0),
            Position(1, 1),
            Position(0, -1),
            Position(0, 1)
        )

        return directions.map { Position(pos.x + it.x, pos.y + it.y) }
            .filter {
                it.x >= fieldSize.first.first &&
                        it.x <= fieldSize.first.second &&
                        it.y >= fieldSize.second.first &&
                        it.y <= fieldSize.second.second
            }
            .map { Pair(it, field[it]!!) }
    }

    private fun getFirstSeatsOccupiedCount(
        pos: Position,
        field: Map<Position, Tile>,
        fieldSize: Pair<Pair<Int, Int>, Pair<Int, Int>>
    ): Int {
        val directions = listOf(
            Position(-1, -1),
            Position(-1, 0),
            Position(-1, 1),
            Position(1, -1),
            Position(1, 0),
            Position(1, 1),
            Position(0, -1),
            Position(0, 1)
        )

        return directions.map {
            var steps = 1
            var foundOccupied = false
            while (true) {
                val currentPos = Position(pos.x + it.x * steps, pos.y + it.y * steps)
                if( currentPos.x >= fieldSize.first.first &&
                    currentPos.x <= fieldSize.first.second &&
                    currentPos.y >= fieldSize.second.first &&
                    currentPos.y <= fieldSize.second.second &&
                    field[currentPos]!! != Tile.EMPTY) {

                    if(field[currentPos]!! == Tile.OCCUPIED){
                        foundOccupied = true
                        break
                    }
                } else {
                     break
                }
                steps++
            }
            if(foundOccupied)  1 else 0
        }.sum()
    }


    private fun parseField(input: List<String>): MutableMap<Position, Tile> {

        val tiles = input.map { row ->
            row.toCharArray().map {
                when (it) {
                    Tile.EMPTY.display -> Tile.EMPTY
                    Tile.FLOOR.display -> Tile.FLOOR
                    else -> Tile.OCCUPIED
                }
            }
        }

        val field = mutableMapOf<Position, Tile>()
        for (y in tiles.indices) {
            for (x in tiles[0].indices) {
                field[Position(x, y)] = tiles[y][x]
            }
        }
        return field
    }


    private fun getFieldSize(field: MutableMap<Position, Tile>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val xSpan = Pair(field.keys.map { it.x }.min()!!, field.keys.map { it.x }.max()!!)
        val ySpan = Pair(field.keys.map { it.y }.min()!!, field.keys.map { it.y }.max()!!)

        return Pair(xSpan, ySpan)
    }


    private fun printField(field: Map<Position, Tile>, size: Pair<Pair<Int, Int>, Pair<Int, Int>>) {
        println("======================================")
        for (y in size.second.first..size.second.second) {
            for (x in size.first.first..size.first.second) {
                print((field[Position(x, y)] ?: error("No value at pos: {$x,$y}")).display)
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

    //private data class Path(val position: Position, val trail: MutableSet<Position>)

    enum class Tile(val display: Char) {
        FLOOR('.'),
        EMPTY('L'),
        OCCUPIED('#')
    }
}