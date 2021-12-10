package adventofcode.util

object AdventOfCodeUtil {
    fun <T> generatePermutations(
        list: List<T>,
        length: Int = list.size,
        result: MutableList<List<T>> = mutableListOf(),
        permutation: List<T> = listOf()
    ): List<List<T>> {
        for (i in list.indices) {
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
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                result.add(Pair(list[i], list[j]))
            }
        }

        return result
    }

    fun <T> combinations(
        set: Set<T>,
        size: Int,
        accumulated: Set<T>,
        combinations: MutableList<Set<T>> = mutableListOf()
    ): List<Set<T>> {
        // 1. stop
        if (set.size < size) {
            return emptyList()
        }
        // 2. add each element in e to accumulated
        when {
            size == 1 -> {
                combinations.addAll(set.map { accumulated.toMutableSet().plus(it) })
            }
            // 3. add all elements in e to accumulated
            set.size == size -> {
                combinations.addAll(listOf(accumulated.plus(set)))
            }
            // 4. for each element, call combination
            set.size > size -> {
                set.forEach { combinations(set.minus(it), size - 1, accumulated.plus(it), combinations) }
            }
        }

        return combinations
    }

    fun getNeighboursXd(pos: List<Int>): Set<List<Int>> {

        val level = pos.size
        val result = mutableSetOf<List<Int>>()

        return getNeighbourCoords(pos, level, result, mutableListOf())
            .filter { !it.all { n -> n == 0 } }
            .map { it.mapIndexed { i, n -> pos[i] + n } }
            .toSet()
    }

    private fun getNeighbourCoords(
        pos: List<Int>,
        level: Int,
        result: MutableSet<List<Int>>,
        neighbour: List<Int>
    ): Set<List<Int>> {

        if (level == 1) {
            setOf(-1, 0, 1).forEach {
                result.add(neighbour.plus(it))
            }
        } else {
            setOf(-1, 0, 1).forEach {
                getNeighbourCoords(pos, level - 1, result, neighbour.plus(it))
            }
        }

        return result
    }

    fun getNeighbours3d(x: Int, y: Int, z: Int): Set<Triple<Int, Int, Int>> {
        return setOf(-1, 0, 1).flatMap { zz ->
            setOf(-1, 0, 1).flatMap { yy ->
                setOf(-1, 0, 1).map { xx ->
                    if (zz == 0 && yy == 0 && xx == 0) null else Triple(x + xx, y + yy, z + zz)
                }
            }
        }.filterNotNull().toSet()
    }

    fun getNeighbours2d(x: Int, y: Int): Set<Pair<Int, Int>> {
        return setOf(-1, 0, 1).flatMap { yy ->
            setOf(-1, 0, 1).map { xx ->
                if (yy == 0 && xx == 0) null else Pair(x + xx, y + yy)
            }
        }.filterNotNull().toSet()
    }

    fun getPerpendicularNeighbours2d(x: Int, y: Int): Set<Pair<Int, Int>> {
        return setOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)).map { pos ->
            Pair(x + pos.first, y + pos.second)
        }.toSet()
    }

    fun greatestCommonDivisor(a: Int, b: Int): Int {
        return if (b == 0) a else greatestCommonDivisor(b, a % b)
    }

    fun greatestCommonDivisor(a: Long, b: Long): Long {
        return if (b == 0L) a else greatestCommonDivisor(b, a % b)
    }

    fun leastCommonMultiple(a: Long, b: Long): Long {
        return a * b / greatestCommonDivisor(a, b)
    }

    fun <T, U> reduceOneToManyMatches(possibleMatches: List<Pair<T, List<U>>>): Map<T, U> {
        val pickedMatch = mutableSetOf<U>()
        return possibleMatches
            .sortedBy { it.second.size }
            .map {
                val chosen = it.second.first { p -> !pickedMatch.contains(p) }
                pickedMatch.add(chosen)
                Pair(it.first, chosen)
            }.toMap()
    }

}