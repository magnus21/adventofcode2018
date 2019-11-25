package adventofcode.v2018

fun main(args: Array<String>) {

    val gridSerialNumber = 4455
    val gridSize = 300

    val fuelCells = mutableListOf<MutableList<FuelCell>>()

    for (y in 1..gridSize) {
        val row = mutableListOf<FuelCell>()
        for (x in 1..gridSize) {
            val powerLevel = calculatePowerLevel(x, y, gridSerialNumber)
            row.add(FuelCell(x, y, powerLevel))
        }
        fuelCells.add(row)
    }


    // Viola Jones: https://www.youtube.com/watch?v=uEJ71VlUmMQ
    val preCalculatedBoxes: HashMap<Pair<Int, Int>, Int> = hashMapOf()
    for (y in 1..gridSize) {
        for (x in 1..gridSize) {
            preCalculatedBoxes.put(
                Pair(x, y),
                preCalculatedBoxes.getOrDefault(Pair(x - 1, y), 0) +
                        preCalculatedBoxes.getOrDefault(Pair(x, y - 1), 0) -
                        preCalculatedBoxes.getOrDefault(Pair(x - 1, y - 1), 0) +
                        fuelCells[y - 1][x - 1].powerLevel
            )
        }
    }

    val mostTotalPowerFuelCellPerGrid = mutableListOf<Pair<FuelCell, Int>>()
    for (squareSize in 1..gridSize) {
        for (y in 1..gridSize - (squareSize - 1)) {
            for (x in 1..gridSize - (squareSize - 1)) {
                //calculateTotalPower(x - 1, y - 1, fuelCells, squareSize)
                violaJones(fuelCells, y, x, preCalculatedBoxes, squareSize)
            }
        }

        val mostTotalPowerFuelCell = fuelCells.flatten().sortedByDescending { it.totalPower }[0]
        mostTotalPowerFuelCellPerGrid.add(Pair(mostTotalPowerFuelCell.copy(), squareSize))

        //println("FuelCell: $mostTotalPowerFuelCell $squareSize")
    }

    mostTotalPowerFuelCellPerGrid.sortByDescending { it.first.totalPower }

    println("FuelCell: " + mostTotalPowerFuelCellPerGrid[0])
}

private fun violaJones(
    fuelCells: MutableList<MutableList<FuelCell>>,
    y: Int,
    x: Int,
    preCalculatedBoxes: HashMap<Pair<Int, Int>, Int>,
    squareSize: Int
) {
   val sum =
            preCalculatedBoxes.getOrDefault(Pair(x + squareSize - 1, y + squareSize - 1), 0) -
            preCalculatedBoxes.getOrDefault(Pair(x - 1, y + squareSize - 1), 0) -
            preCalculatedBoxes.getOrDefault(Pair(x + squareSize - 1, y - 1), 0) +
            preCalculatedBoxes.getOrDefault(Pair(x - 1, y - 1), 0)

    fuelCells[y - 1][x - 1].totalPower = sum
}

data class FuelCell(val x: Int, val y: Int, val powerLevel: Int, var totalPower: Int = 0)

fun calculateTotalPower(
    x: Int,
    y: Int,
    fuelCells: MutableList<MutableList<FuelCell>>,
    squareSize: Int
) {
    var totalPower = 0
    for (yy in 0..(squareSize - 1)) {
        for (xx in 0..(squareSize - 1)) {
            totalPower += fuelCells[y + yy][x + xx].powerLevel
        }
    }

    fuelCells[y][x].totalPower = totalPower
}

fun calculatePowerLevel(x: Int, y: Int, gridSerialNumber: Int): Int {
    val rackId = x + 10
    return (((rackId * y + gridSerialNumber) * rackId / 100) % 10) - 5
}