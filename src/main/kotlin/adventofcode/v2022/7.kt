package adventofcode.v2022

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import kotlin.time.ExperimentalTime

object Day7 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val input = FileParser.getFileRows(2022, "7.txt")

        val root = parseInput(input)
        printResult("part 1") {
            val result = mutableListOf<Long>()
            getDirSizes(root, result)
            result.filter { it <= 100000 }.sumOf { it }
        }
        printResult("part 2") {
            val totalSize = getSize(root)
            val freeSpace = 70000000L - totalSize
            val neededSpace = 30000000L - freeSpace

            val result = mutableListOf<Long>()
            getDirSizes(root, result)
            result.sorted().first { it >= neededSpace }
        }
    }

    private fun getDirSizes(file: File, result: MutableList<Long>) {
        if (file.children != null) {
            result.add(getSize(file))
            file.children.forEach { getDirSizes(it, result) }
        }
    }

    private fun getSize(file: File): Long {
        if (file.children != null) {
            return file.children.sumOf { getSize(it) }
        }
        return file.size
    }

    private fun parseInput(rows: List<String>): File {
        val root = File("/", 0, null, mutableListOf())
        parseDir(root, rows.drop(1).filter { it != "\$ ls" })
        return root
    }

    private fun parseDir(parent: File, rows: List<String>) {

        var workDir = parent
        for (row in rows) {
            val parts = row.split(" ")
            if (row.startsWith("dir")) {
                workDir.children?.add(File(parts[1], 0, workDir, mutableListOf()))
            } else if (row.matches("\\$ cd [a-z]+".toRegex())) {
                workDir = workDir.children?.find { it.name == parts[2] }!!
            } else if (row == "\$ cd ..") {
                workDir = workDir.parent!!
            } else {
                workDir.children?.add(File(parts[1], parts[0].toLong(), workDir, null))
            }
        }
    }

    data class File(val name: String, val size: Long, val parent: File?, val children: MutableList<File>?)
}