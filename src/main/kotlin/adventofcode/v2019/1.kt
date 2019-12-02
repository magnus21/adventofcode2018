package adventofcode.v2019

import adventofcode.util.FileParser

fun main(args: Array<String>) {

    val modulesMasses = FileParser.getFileRows(2019, "1.txt").map { Integer.valueOf(it) }

    // Fuel sum.
    println(modulesMasses.map(Day1::fuel).sum())

    // Fuel sum with fuel taken into account.
    println(modulesMasses.map { Day1.fuelWithFuelTakenIntoAccount(it) }.sum())
}

object Day1 {

    fun fuel(mass: Int): Int {
        return mass / 3 - 2
    }

    tailrec fun fuelWithFuelTakenIntoAccount(mass: Int, accFuel: Int = 0): Int {
        val fuel = fuel(mass)
        return if (fuel <= 0) accFuel else fuelWithFuelTakenIntoAccount(fuel, accFuel + fuel)
    }
}