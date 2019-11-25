package adventofcode.v2018

import adventofcode.util.FileParser

fun main(args: Array<String>) {

    val frequencies = FileParser.getFileRows(2019, "1.txt").map { Integer.valueOf(it) }

    // Sum freqs.
    println(frequencies.sum())

    // First repeated accumulative freq sum.
    println(findRepeatedAccSum(frequencies, 0, mutableSetOf(0)))
}

private fun findRepeatedAccSum(
    frequencies: List<Int>,
    startSum: Int,
    sums: MutableSet<Int>
): Int {
    var accSum = startSum
    for (freq in frequencies) {
        accSum += freq
        if (sums.contains(accSum)) {
            return accSum;
        }
        sums.add(accSum);
    }

    return findRepeatedAccSum(frequencies, accSum, sums)
}