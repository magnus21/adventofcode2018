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

    fun <T> combinations(set: Set<T>, size: Int, accumulated: Set<T>, combinations: MutableList<Set<T>> = mutableListOf()): List<Set<T>> {
        // 1. stop
        if (set.size < size) {
            return emptyList()
        }
        // 2. add each element in e to accumulated
        if (size == 1) {
            combinations.addAll(set.map { accumulated.toMutableSet().plus(it) })
        }
        // 3. add all elements in e to accumulated
        else if (set.size == size) {
            combinations.addAll(listOf(accumulated.plus(set)))
        }
        // 4. for each element, call combination
        else if (set.size > size) {
            set.forEach { combinations(set.minus(it), size - 1, accumulated.plus(it), combinations) }
        }

        return combinations
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