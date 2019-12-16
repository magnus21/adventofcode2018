package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.system.measureTimeMillis

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "14.txt")
        val map = parseInput(input)

        var result1 = 0L
        val time1 = measureTimeMillis {
            val reserve = mutableMapOf<String, Long>()

            result1 = getOreForOneFuel(map, reserve, ChemicalAmount(1, "FUEL"))
            println("Result part 1: $result1")
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {
            val reserve = mutableMapOf<String, Long>()

            // Brut force this shit.
            var ore = 1000000000000
            var count = 0
            while (ore >= 0) {
                val result = getOreForOneFuel(map, reserve, ChemicalAmount(1, "FUEL"))
                ore -= result
                count++
                if (count % 100000 == 0) {
                    println("Ore left: $ore")
                }
            }
            // Correct answer: 13108426
            println("Result part 2: ${count - 1}")
        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun getOreForOneFuel(
        map: Map<ChemicalAmount, List<ChemicalAmount>>,
        reserve: MutableMap<String, Long>,
        startChemicalAmount: ChemicalAmount
    ): Long {
        var oreAmount = 0L

        val queue = Queue<ChemicalAmount>()
        queue.enqueue(startChemicalAmount)

        while (queue.isNotEmpty()) {
            val chemical = queue.dequeue()!!
            if (chemical.code == "ORE") {
                oreAmount += chemical.amount
            } else {
                processItem(chemical, map, queue, reserve)
            }
        }
        return oreAmount
    }

    private fun processItem(
        chemical: ChemicalAmount,
        map: Map<ChemicalAmount, List<ChemicalAmount>>,
        queue: Queue<ChemicalAmount>,
        reserve: MutableMap<String, Long>
    ) {
        val chemicalAmount = chemical.amount
        val chemicalCode = chemical.code
        val key = map.keys.filter { it.code == chemicalCode }[0]

        val reserveAmount = reserve.getOrDefault(chemicalCode, 0)

        val reserveUsage = if (reserveAmount <= chemicalAmount) reserveAmount else chemicalAmount
        reserve[chemicalCode] = reserveAmount - reserveUsage

        val adjustedChemicalAmount = chemicalAmount - reserveUsage

        val counter =
            if (adjustedChemicalAmount == 0L) 0L else ((adjustedChemicalAmount / key.amount).toInt() + (if (adjustedChemicalAmount % key.amount != 0L) 1L else 0L))
        val rest = if (adjustedChemicalAmount == 0L) 0L else counter * key.amount - adjustedChemicalAmount

        reserve[chemicalCode] = reserve.getOrDefault(chemicalCode, 0) + rest

        map[key]!!.forEach {
            if (counter > 0) {
                queue.enqueue(ChemicalAmount(it.amount * counter, it.code))
            }
        }
    }

    private fun parseInput(rows: List<String>): Map<ChemicalAmount, List<ChemicalAmount>> {
        val map: MutableMap<ChemicalAmount, List<ChemicalAmount>> = mutableMapOf()
        rows.forEach { row ->
            val inOut = row.split("=>")
            val inputChemicals =
                inOut[0].split(",").map { it.trim() }
                    .map { ChemicalAmount(it.split(" ")[0].toLong(), it.split(" ")[1]) }

            val outputChemicalData = inOut[1].trim().split(" ").map { it.trim() }

            map[ChemicalAmount(outputChemicalData[0].toLong(), outputChemicalData[1])] = inputChemicals
        }
        return map
    }

    data class ChemicalAmount(val amount: Long, val code: String, val reduceOre: Boolean = false) {
        override fun toString(): String {
            return "$amount $code"
        }
    }

}