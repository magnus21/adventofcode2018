package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.shared.IntCodeComputer

fun main(args: Array<String>) {

    val modulesMasses = FileParser.getFileRows(2019, "1.txt").map { Integer.valueOf(it) }

    // Fuel sum.
    println(modulesMasses.map(Day1::fuel).sum())
    // 3297896

    // IntCodeComputer (adds ints in a list)
//
//    val division = "1001"
//    val intCodeProgram = "03,0,1101,0,0,1,1101,0,0,2,03,2,0001,1,2,1,1001,3,1,3,0007,3,0,4,1005,4,8,0004,1,99"
//        .split(",")
//        .map { Integer.valueOf(it) }
//
//    val test = listOf(21,4,7)
//    val inputIntCodeProgram = listOf(test.size) + test
//    val result = IntCodeComputer(intCodeProgram.toMutableList()).runWithInput(inputIntCodeProgram)
   // println(result.first[0])

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