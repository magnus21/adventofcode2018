package adventofcode.v2021

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.math.abs
import kotlin.system.measureTimeMillis

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {
        val scanners = parseInput(FileParser.getFileRows(2021, "19.txt"))
        scanners.forEach { scanner ->
            computeBeaconRelativesDistances(scanner)
        }

        val time1 = measureTimeMillis {
            val overlaps = mutableListOf<ScannerOverlap>()
            val normalizedScanners = mutableSetOf(scanners[0].name)
            scanners[0].normalizedPosition = listOf(0, 0, 0)
            val processedReferenceScanners = mutableSetOf<String>()

            while (normalizedScanners.size < scanners.size) {
                val referenceScannerName = normalizedScanners.first { !processedReferenceScanners.contains(it) }
                val referenceScanner = scanners.first { it.name == referenceScannerName }
                scanners
                    .filter { !normalizedScanners.contains(it.name) }
                    .filter { !processedReferenceScanners.contains(it.name) }
                    .forEach { otherScanner ->
                        findOverLappingBeacons(referenceScanner, otherScanner)?.let {
                            val overlap = ScannerOverlap(referenceScanner, otherScanner, it.second, it.first)
                            setRelativeDistance(overlap)
                            adjust(otherScanner, overlap)
                            normalizedScanners.add(otherScanner.name)
                            overlaps.add(overlap)
                        }
                    }
                processedReferenceScanners.add(referenceScanner.name)
            }

            val beacons = scanners.flatMap { it.beacons }.toSet()

            println("answer part 1: ${beacons.size}")

            val scannerPositions = scanners.map { it.normalizedPosition }
            val answer2 = scannerPositions.flatMapIndexed { i, pos1 ->
                scannerPositions
                    .filterIndexed { j, _ -> j > i }
                    .map { pos2 ->
                        getAbsDistance(pos1!!, pos2!!).sum()
                    }
            }.maxOrNull()!!

            println("answer part 2: $answer2")
        }
        println("Time: $time1 ms")
    }

    private fun adjust(scanner: Scanner, overlap: ScannerOverlap) {
        val permReverseMap = overlap.perm.mapIndexed { i, o -> Pair(o, i) }.toMap()
        val adjustedBeacons = scanner.beacons.map { beacon ->
            beacon.mapIndexed { i, v ->
                val (diff, sign) = overlap.distToOther!![permReverseMap[i]!!]
                Pair(sign * v + diff, permReverseMap[i]!!)
            }.sortedBy { it.second }.map { it.first }
        }
        scanner.beacons.clear()
        scanner.beacons.addAll(adjustedBeacons)

        scanner.relativeBeaconDistances.clear()
        computeBeaconRelativesDistances(scanner)
        scanner.normalizedPosition = overlap.distToOther!!.map { it.first }
    }

    private val signPermutations = listOf(1, -1)
    private fun setRelativeDistance(overlap: ScannerOverlap) {
        val otherRelativePos = mutableListOf<Pair<Int, Int>>()
        for (i in (0..2)) {
            for (sign in signPermutations) {
                val diffs = overlap.beaconMatches.map { match ->
                    sign * match.otherBeacon[overlap.perm[i]] - match.beacon[i]
                }
                if (diffs.distinct().size == 1) {
                    otherRelativePos.add(Pair(-diffs.first(), sign))
                    break
                }
            }
        }
        overlap.distToOther = otherRelativePos
    }

    private val permutations = AdventOfCodeUtil.generatePermutations(listOf(0, 1, 2))
    private fun findOverLappingBeacons(scanner1: Scanner, scanner2: Scanner): Pair<List<Int>, Set<BeaconMatch>>? {
        for (perm in permutations) {
            val overlaps = mutableSetOf<BeaconMatch>()
            scanner1.relativeBeaconDistances.forEach { beacon1 ->
                scanner2.relativeBeaconDistances.forEach { beacon2 ->
                    val intersect =
                        beacon1.value.map { it.distance }.intersect(beacon2.value.map { transform(it.distance, perm) })
                    if (intersect.size >= 11) {
                        overlaps.add(BeaconMatch(beacon1.key, beacon2.key))
                    }
                }
            }
            if (overlaps.size >= 12) {
                return Pair(perm, overlaps)
            }
        }
        return null
    }

    private fun transform(dist: List<Int>, order: List<Int>): List<Int> {
        return order.map { dist[it] }
    }

    private fun computeBeaconRelativesDistances(scanner: Scanner) {
        scanner.beacons.forEach { beacon ->
            scanner.beacons
                .filter { it != beacon }
                .forEach { otherBeacon ->
                    scanner.relativeBeaconDistances.computeIfAbsent(beacon) { mutableSetOf() }
                        .add(BeaconDistance(otherBeacon, getAbsDistance(beacon, otherBeacon)))
                }
        }
    }

    private fun getAbsDistance(from: List<Int>, to: List<Int>): List<Int> {
        return to.indices.map { i -> abs(to[i] - from[i]) }
    }


    private fun parseInput(fileRows: List<String>): List<Scanner> {
        return fileRows.fold(mutableListOf()) { acc, row ->
            when {
                row.startsWith("--") -> acc.add(Scanner(row.replace("-", "").replace(" ", "")))
                row.isNotEmpty() -> {
                    val parts = row.split(",").map { it.toInt() }
                    acc.last().beacons.add(listOf(parts[0], parts[1], parts[2]))
                }
            }
            acc
        }
    }

    data class Scanner(
        val name: String,
        val beacons: MutableList<List<Int>> = mutableListOf(),
        val relativeBeaconDistances: MutableMap<List<Int>, MutableSet<BeaconDistance>> = mutableMapOf(),
        var normalizedPosition: List<Int>? = null
    )

    data class BeaconDistance(val beacon: List<Int>, val distance: List<Int>)
    data class BeaconMatch(var beacon: List<Int>, val otherBeacon: List<Int>)
    data class ScannerOverlap(
        val scanner: Scanner,
        val otherScanner: Scanner,
        val beaconMatches: Set<BeaconMatch>,
        var perm: List<Int>,
        var distToOther: List<Pair<Int, Int>>? = null
    )
}