package adventofcode.v2018

import java.io.File
import java.util.*

fun main(args: Array<String>) {

    val boxIds = File("src/main/resources/2.txt").readLines()

    println(
        findNrOfBoxIdsWithCountOfALetter(
            2,
            boxIds
        ) * findNrOfBoxIdsWithCountOfALetter(3, boxIds)
    )

    println(findCommonIdPart(boxIds))
}

fun findNrOfBoxIdsWithCountOfALetter(count: Int, boxIds: List<String>): Int {
    return boxIds.filter { id -> hasLetterWithCount(id, count) }.size
}

fun hasLetterWithCount(id: String, count: Int): Boolean {
    return id.groupBy { it }.filter { entry -> entry.value.size == count }.isNotEmpty()
}

fun findCommonIdPart(boxIds: List<String>): Optional<String> {
    for (id1 in boxIds) {
        for (id2 in boxIds) {
            if (id1.length == id2.length) {
                var diffCount = 0
                var idWithOutDiff = ""
                for (i in 0 until id1.length) {
                    if (id1[i] != id2[i]) {
                        diffCount++
                    } else {
                        idWithOutDiff += id1[i]
                    }
                    if (diffCount > 1) {
                        break;
                    }
                }
                if (diffCount == 1) {
                    return Optional.of(idWithOutDiff)
                }
            }
        }
    }

    return Optional.empty()
}