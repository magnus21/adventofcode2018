package adventofcode

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

    val mostTotalPowerFuelCellPerGrid = mutableListOf<Pair<FuelCell,Int>>()
    for (squareSize in 1..12){
        for (y in 1..gridSize - (squareSize - 1)) {
            for (x in 1..gridSize - (squareSize - 1)) {
                calculateTotalPower(x - 1, y - 1, fuelCells, squareSize)
            }
        }

        val mostTotalPowerFuelCell = fuelCells.flatten().sortedByDescending { it.totalPower }[0]
        mostTotalPowerFuelCellPerGrid.add(Pair(mostTotalPowerFuelCell.copy(), squareSize))
    }

    mostTotalPowerFuelCellPerGrid.sortByDescending { it.first.totalPower }

    println("FuelCell: " + mostTotalPowerFuelCellPerGrid[0])
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