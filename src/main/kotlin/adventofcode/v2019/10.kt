package adventofcode.v2019

import adventofcode.util.FileParser

object Day10 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "10.txt")

        val (fieldSize, field) = parseField(input)

        // Part 1
        part1(field)
        //printField(field, fieldSize, true)
        val station = field.entries.sortedByDescending { it.value }[0]
        println(station)

        // Part 2
        val asteroid200 = part2(field, station.key, 200)
        println(asteroid200)
        println(asteroid200.x * 100 + asteroid200.y)
    }

    private fun part1(field: MutableMap<FieldPoint, Int>) {
        field.keys.toMutableList().forEach {
            field.keys.toMutableList().forEach { other ->
                if (it != other) {
                    val distance = getManhattanDistanceBetweenPoints(it, other)
                    val step = reduceFraction(distance)

                    var count = 1
                    while (true) {
                        val point = FieldPoint(it.x + step.first * count, it.y + step.second * count)

                        if (point == other) {
                            field[it] = field[it]!! + 1
                            break
                        } else if (field.contains(point)) {
                            break
                        }
                        count++
                    }
                }
            }
        }
    }

    private fun part2(field: MutableMap<FieldPoint, Int>, station: FieldPoint, nr: Int): FieldPoint {

        val angles = field.keys.toMutableList()
            .filter { it != station }
            .groupBy {
                val distPair = getManhattanDistanceBetweenPoints(station, it)
                Pair(getAngleBetweenPoints(distPair), getEuclideanDistance(distPair))
            }
            .toSortedMap(compareBy<Pair<Double, Double>> { it.first }.thenBy { it.second })

        val taken = mutableSetOf<FieldPoint>()
        var count = 0
        while (true) {
            angles.keys.map { it.first }.distinct().forEach { angle ->
                val points = angles
                    .filter { it.key.first == angle }
                    .filter { !taken.contains(it.value[0]) }
                    .values.flatten()

                if (points.isNotEmpty()) {
                    taken.add(points[0])
                    //println("Nr $count: angle: $angle, $point")
                    if (++count == nr) {
                        return points[0]
                    }
                }
            }
        }
    }

    data class FieldPoint(val x: Int, val y: Int)

    private fun reduceFraction(point: Pair<Int, Int>): Pair<Int, Int> {
        val d = greatestCommonDivisor(Math.abs(point.first), Math.abs(point.second))
        return Pair(point.first / d, point.second / d)
    }

    private fun greatestCommonDivisor(a: Int, b: Int): Int {
        return if (b == 0) a else greatestCommonDivisor(b, a % b)
    }

    private fun getManhattanDistanceBetweenPoints(it: FieldPoint, other: FieldPoint): Pair<Int, Int> {
        return Pair(other.x - it.x, other.y - it.y)
    }

    private fun getEuclideanDistance(distPair: Pair<Int, Int>) =
        Math.sqrt(Math.pow(distPair.first.toDouble(), 2.0) + Math.pow(distPair.second.toDouble(), 2.0))

    private fun getAngleBetweenPoints(distPair: Pair<Int, Int>): Double {
        val xDiff = distPair.first.toDouble()
        val angle = Math.atan(distPair.second.toDouble() / xDiff) + Math.PI / 2

        if (xDiff < 0) {
            return angle + Math.PI
        }
        return angle
    }


    private fun parseField(input: List<String>): Pair<Pair<Int, Int>, MutableMap<FieldPoint, Int>> {

        val fieldSize = Pair(input[0].length, input.size)
        val fieldPoints = mutableMapOf<FieldPoint, Int>()

        for (y in 0 until fieldSize.second) {
            val row = input[y]
            for (x in 0 until fieldSize.first) {
                if (row[x] == '#') {
                    fieldPoints.put(FieldPoint(x, y), 0)
                }
            }
        }

        return Pair(fieldSize, fieldPoints)
    }

    private fun printField(
        field: MutableMap<FieldPoint, Int>,
        fieldSize: Pair<Int, Int>,
        showCount: Boolean = false
    ) {
        for (y in 0 until fieldSize.second) {
            for (x in 0 until fieldSize.first) {
                val count = field[FieldPoint(x, y)]
                if (count != null) print(if (showCount) count else "#") else print(".")
            }
            println()
        }
    }

}


