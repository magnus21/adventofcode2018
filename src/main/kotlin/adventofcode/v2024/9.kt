package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day9 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val diskMap = parseInput(FileParser.getFileRows(2024, "9.txt"))

        printResult("part 1") { part1(diskMap) }
        printResult("part 2") { part2(diskMap) }
        // 6398065450842
    }

    private fun part1(diskMap: List<Int>): Long {
        val diskMapDecoded = decodeDiskMap(diskMap)

        val mutableDiskMap = diskMapDecoded.toMutableList()
        while (haveGaps(mutableDiskMap)) {
            val fromIndex = mutableDiskMap.indexOfLast { it != "." }
            val toIndex = mutableDiskMap.indexOf(".")
            mutableDiskMap[toIndex] = mutableDiskMap[fromIndex]
            mutableDiskMap[fromIndex] = "."
        }
        println(mutableDiskMap.joinToString("") { it })
        return mutableDiskMap.takeWhile { it != "." }.mapIndexed { i, n -> i * n.toLong() }.sum()
    }

    private fun part2(diskMap: List<Int>): Long {
        val diskMapDecoded = decodeDiskMap(diskMap)
        val distinctFileIds = diskMapDecoded.filter { it != "." }.distinct().size

        val processedFileIds = mutableSetOf<String>()
        val chunkedDiskMap = createChunkedDiskMap(diskMapDecoded)

        val mutableDiskMap = chunkedDiskMap.toMutableList()
        while (processedFileIds.size < distinctFileIds) {
            val chunkToProcess =
                mutableDiskMap.mapIndexed { i, p -> Pair(i, p) }.filter { !processedFileIds.contains(it.second.first) }
                    .last { it.second.first != "." }
            val freeChunk = mutableDiskMap.mapIndexed { i, p -> Pair(i, p) }
                .filter { it.first < chunkToProcess.first }
                .firstOrNull { it.second.first == "." && it.second.second.size >= chunkToProcess.second.second.size }
            if (freeChunk != null) {
                move(chunkToProcess, freeChunk, mutableDiskMap)
            }
            processedFileIds.add(chunkToProcess.second.first)
        }

        return mutableDiskMap.flatMap { it.second }.mapIndexed { i, n -> if (n == ".") 0 else i * n.toLong() }.sum()
    }

    private fun move(
        chunk: Pair<Int, Pair<String, MutableList<String>>>,
        freeChunk: Pair<Int, Pair<String, MutableList<String>>>?,
        mutableDiskMap: MutableList<Pair<String, MutableList<String>>>
    ) {
        val freeChunkIndex = freeChunk!!.first
        mutableDiskMap[freeChunkIndex] = chunk.second

        mutableDiskMap[chunk.first] = Pair(".", (1..chunk.second.second.size).map { "." }.toMutableList())
        val leftOver = freeChunk.second.second.size - chunk.second.second.size
        if (leftOver > 0) {
            mutableDiskMap.add(freeChunkIndex + 1, Pair(".", (1..leftOver).map { "." }.toMutableList()))
        }
    }

    private fun createChunkedDiskMap(mutableDiskMap: List<String>) =
        mutableDiskMap.fold(Pair(mutableListOf<Pair<String, MutableList<String>>>(), "")) { acc, s ->
            if (acc.second == s) {
                acc.first.last().second.add(s)
            } else {
                acc.first.add(Pair(s, mutableListOf(s)))
            }
            Pair(acc.first, s)
        }.first

    private fun haveGaps(diskMap: List<String>): Boolean {
        val firstIndex = diskMap.indexOf(".")
        val lastIndex = diskMap.lastIndexOf(".")
        return firstIndex != -1 && diskMap.subList(firstIndex + 1, lastIndex).any { it != "." }
    }

    private fun decodeDiskMap(diskMap: List<Int>): List<String> {
        return diskMap.foldIndexed(listOf()) { i, acc, number ->
            if (i % 2 == 0) acc.plus((1..number).map { (i / 2).toString() })
            else acc.plus((1..number).map { "." })
        }
    }

    private fun parseInput(input: List<String>) =
        input[0].toCharArray().map { it.toString().toInt() }
}