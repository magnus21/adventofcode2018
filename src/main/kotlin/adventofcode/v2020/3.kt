package adventofcode.v2020

import adventofcode.util.FileParser

object Day3 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "3.txt")

        val (fieldSize, field) = parseField(input)

        //printField(field, fieldSize)

        // Part 1
        part1(field, fieldSize, 3, 1)

        // Part 1
        part2(field, fieldSize)
    }

    private fun part1(
        field: MutableMap<FieldPoint, Int>,
        fieldSize: Pair<Int, Int>,
        left: Int,
        down: Int
    ) {
        val treeCount = traversAndCountTrees(fieldSize, field, left, down)

        println("Part 1: $treeCount")
    }

    private fun part2(
        field: MutableMap<FieldPoint, Int>,
        fieldSize: Pair<Int, Int>
    ) {
        val slopes = listOf(
            Pair(1, 1),
            Pair(3, 1),
            Pair(5, 1),
            Pair(7, 1),
            Pair(1, 2)
        )

        val treeCountProduct = slopes.map { traversAndCountTrees(fieldSize, field, it.first, it.second) }
            .map { it.toLong() }
            .reduce { acc, i -> acc * i }

        println("Part 2: $treeCountProduct")
    }

    private fun traversAndCountTrees(
        fieldSize: Pair<Int, Int>,
        field: MutableMap<FieldPoint, Int>,
        left: Int,
        down: Int
    ): Int {
        var posX = 0
        var posY = 0
        var treeCount = 0

        while (posY < fieldSize.second) {
            posX += left
            posY += down

            if (posX >= fieldSize.first) {
                posX %= fieldSize.first
            }

            if (field[FieldPoint(posX, posY)] == 1) {
                treeCount++
            }
        }
        return treeCount
    }


    data class FieldPoint(val x: Int, val y: Int)

    private fun parseField(input: List<String>): Pair<Pair<Int, Int>, MutableMap<FieldPoint, Int>> {

        val fieldSize = Pair(input[0].length, input.size)
        val fieldPoints = mutableMapOf<FieldPoint, Int>()

        for (y in 0 until fieldSize.second) {
            val row = input[y]
            for (x in 0 until fieldSize.first) {
                if (row[x] == '#') {
                    fieldPoints[FieldPoint(x, y)] = 1
                }
            }
        }

        return Pair(fieldSize, fieldPoints)
    }

    private fun printField(
        field: MutableMap<FieldPoint, Int>,
        fieldSize: Pair<Int, Int>
    ) {
        for (y in 0 until fieldSize.second) {
            for (x in 0 until fieldSize.first) {
                val tree = field[FieldPoint(x, y)]
                if (tree != null) print("#") else print(".")
            }
            println()
        }
    }

}


