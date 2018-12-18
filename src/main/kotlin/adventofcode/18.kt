package adventofcode

import adventofcode.AcerState.*
import java.io.File

fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/18.txt").readLines().map { it.toCharArray() }
    val acers = parseAcers(rawInput)

    printAcers(acers)

    val limit = 1000000000
    var minutes = 0
    val mutableAcers = acers.toList()
    val hashCodeMap = mutableMapOf<Int, Int>()
    do {
        val acersSnapShot = mutableAcers.map { it.map { it.copy() } }

        mutableAcers.forEach { acerRow ->
            acerRow.forEach { acer ->
                val neighbours = getAcerNeighbours(acer, acersSnapShot)
                when (acer.state) {
                    OPEN -> processOpen(acer, neighbours)
                    TREES -> processTree(acer, neighbours)
                    LUMBER -> processLumber(acer, neighbours)
                }
            }
        }

        if (minutes % 100 == 0) {
            println("minutes: $minutes")
            printAcers(mutableAcers)
        }

        // Check for repeating pattern of acers.
        val hashCode = mutableAcers.hashCode()
        //println("Minute: $minutes - matches" + hashCodeMap.entries.filter {  it.value == hashCode}.map {  it.key })
        hashCodeMap.put(minutes, hashCode)

        // Repeating started at 605 and by 28.
        if (minutes == 605) {
            // Fast forward to the end with some safety margin.
            while (minutes < limit - 28 + 2) {
                minutes += 28
            }
        }

    } while (++minutes < limit)

    printAcers(mutableAcers)

    // Part 1: 248920, part 2: 193050
    println(mutableAcers.flatten().filter { it.state == TREES }.count() * acers.flatten().filter { it.state == LUMBER }.count())
}

fun getAcerNeighbours(acer: Acer, acersSnapShot: List<List<Acer>>): List<Acer> {
    return acersSnapShot.flatten().filter { isNeighbour(it, acer) }
}

fun isNeighbour(it: Acer, acer: Acer): Boolean {
    val xDiff = Math.abs(it.x - acer.x)
    val yDiff = Math.abs(it.y - acer.y)

    return (xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1) || (xDiff == 1 && yDiff == 1)
}

fun processLumber(acer: Acer, neighbours: List<Acer>) {
    val hasLumberNeighbour = neighbours.filter { it.state == LUMBER }.any()
    val hasTreeNeighbour = neighbours.filter { it.state == TREES }.any()

    if (!hasLumberNeighbour || !hasTreeNeighbour) {
        acer.state = OPEN
    }
}

fun processOpen(acer: Acer, neighbours: List<Acer>) {
    val hasAtLeastThreeTreeNeighbours = neighbours.filter { it.state == TREES }.count() >= 3

    if (hasAtLeastThreeTreeNeighbours) {
        acer.state = TREES
    }
}

fun processTree(acer: Acer, neighbours: List<Acer>) {
    val hasAtLeastThreeLumberNeighbours = neighbours.filter { it.state == LUMBER }.count() >= 3

    if (hasAtLeastThreeLumberNeighbours) {
        acer.state = LUMBER
    }
}

data class Acer(var x: Int, var y: Int, var state: AcerState)
enum class AcerState(val code: String) {
    OPEN("."), TREES("|"), LUMBER("#")
}


fun parseAcers(rawInput: List<CharArray>): List<List<Acer>> {
    val acers = mutableListOf<List<Acer>>()

    for (y in 0 until rawInput.size) {
        val row = mutableListOf<Acer>()
        for (x in 0 until rawInput[y].size) {
            val input = rawInput[y][x]

            val state = when (input) {
                '|' -> TREES
                '#' -> LUMBER
                else -> OPEN
            }
            row.add(Acer(x, y, state))
        }
        acers.add(row)
    }

    return acers
}

private fun printAcers(acers: List<List<Acer>>) {
    acers.forEach { row ->
        run {
            row.forEach {
                print(it.state.code)
            }
            println()
        }
    }
    println()
}