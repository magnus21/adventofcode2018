package adventofcode.util

object AdventOfCodeUtil {
    fun <T> generatePermutations(
        list: List<T>,
        length: Int,
        result: MutableList<List<T>> = mutableListOf(),
        permutation: List<T> = listOf()
    ): List<List<T>> {
        for (i in 0 until list.size) {
            if (permutation.size == length - 1) {
                result.add(permutation.plusElement(list[i]))
                break
            }

            val listCopy = list.toMutableList()
            listCopy.removeAt(i)
            generatePermutations(listCopy, length, result, permutation.plusElement(list[i]))
        }

        return result
    }

    fun <T> generatePairs(list: List<T>): MutableList<Pair<T, T>> {
        val result = mutableListOf<Pair<T, T>>()
        for (i in 0 until list.size) {
            for (j in i + 1 until list.size) {
                result.add(Pair(list[i], list[j]))
            }
        }

        return result
    }

    fun greatestCommonDivisor(a: Int, b: Int): Int {
        return if (b == 0) a else greatestCommonDivisor(b, a % b)
    }

    fun greatestCommonDivisor(a: Long, b: Long): Long {
        return if (b == 0L) a else greatestCommonDivisor(b, a % b)
    }

    fun leastCommonMultiple(a: Long, b: Long): Long {
        return a * b / greatestCommonDivisor(a, b);
    }

}