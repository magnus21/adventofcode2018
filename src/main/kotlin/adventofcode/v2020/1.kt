package adventofcode.v2020

import adventofcode.util.FileParser
import java.util.Optional
import java.util.Optional.empty
import java.util.Optional.of
import kotlin.system.measureTimeMillis

fun main() {

    val entries = FileParser.getFileRows(2020, "1.txt").map { Integer.valueOf(it) }


    val time1 = measureTimeMillis {
        println("answer part 1: " + Day1.get2020EntriesProduct(entries))
    }
    println("Time: $time1 ms")

    val time2 = measureTimeMillis {
        println("answer part 2: " + Day1.get2020ThreeEntriesProduct(entries))
    }
    println("Time: $time2 ms")
}

object Day1 {
    fun get2020EntriesProduct(entries: List<Int>): Optional<Int> {

        for (index in entries.indices) {
            val first = entries[index]
            entries.drop(index).forEach { if (first + it == 2020) return of(first * it) }
        }
        return empty()
    }

    fun get2020ThreeEntriesProduct(entries: List<Int>): Optional<Int> {

        for (index1 in entries.indices) {
            val first = entries[index1]
            for (index2 in (index1 + 1) until entries.size) {
                val second = entries[index2]
                entries.drop(index2).forEach { if (first + second + it == 2020) return of(first * second * it) }
            }
        }
        return empty()
    }
}