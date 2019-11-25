package adventofcode.v2018

import java.io.File
import java.lang.Integer.parseInt

fun main(args: Array<String>) {

    val claims = File("src/main/resources/3.txt").readLines()

    val coordinatesMap = mutableMapOf<Pair<Int, Int>, Int>()

    // Overlapping coordinates.
    println(claims.fold(coordinatesMap) { acc, claim -> processClaim(acc, claim) }.filter { (_, v) -> v > 1 }.size)

    // Claims without overlapping
    println(claims.filter { claim -> parseClaim(claim).none { coordinate -> coordinatesMap[coordinate]!! > 1 } })
}

fun processClaim(acc: MutableMap<Pair<Int, Int>, Int>, claim: String): MutableMap<Pair<Int, Int>, Int> {
    parseClaim(claim).forEach { coordinate -> acc.compute(coordinate) { _, oldValue -> if (oldValue == null) 1 else oldValue + 1 } }
    return acc
}

fun parseClaim(claim: String): Set<Pair<Int, Int>> {
    val claimParts: List<String> = claim.split(" ", ",", "x", ":")
    val (xs, ys) = Pair(parseInt(claimParts[2]), parseInt(claimParts[3]))
    val (w, h) = Pair(parseInt(claimParts[5]), parseInt(claimParts[6]))

    return (0 until w).flatMap { x -> (0 until h).map { y -> Pair(xs + x, ys + y) } }.toSet()
}
