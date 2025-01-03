package adventofcode.v2024

import adventofcode.util.AdventOfCodeUtil.printResult
import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.time.ExperimentalTime

object Day19 {

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val (patterns, designs) = parseInput(FileParser.getFileRows(2024, "19.txt"))

        printResult("part 1") { solve(patterns, designs) }
        printResult("part 2") { solve2(patterns, designs) }
    }

    private fun solve(patterns: List<String>, designs: List<String>): Int {
        return designs.filter { possibleDesign(it, patterns, "") }.size
    }

    private fun possibleDesign(design: String, patterns: List<String>, result: String): Boolean {
        if (result == design) {
            return true
        }
        return patterns
            .map { result + it }
            .filter { it.length <= design.length && design.substring(0, it.length) == it }
            .any { possibleDesign(design, patterns, it) }
    }

    private fun solve2(patterns: List<String>, designs: List<String>): Long {
        val parts = designs.map { design -> findPathCount(design, patterns) }
        return parts.sumOf { it }
    }

    private fun findPathCount(
        design: String,
        patterns: List<String>
    ): Long {
        val paths = mutableListOf<PatternPath>()
        val queue = Queue<PatternPath>()
        queue.enqueue(PatternPath())

        while (queue.isNotEmpty()) {
            val path = queue.dequeue()!!
            if (path.design == design) {
                paths.add(path)
            } else {
                explorePath(path, design, patterns, queue)
            }
        }
        return paths.sumOf { it.multiplier }
    }

    private fun explorePath(
        basePath: PatternPath,
        design: String,
        patterns: List<String>,
        queue: Queue<PatternPath>
    ) {
        patterns
            .map { PatternPath(basePath.steps.plus(it), basePath.design.plus(it), basePath.multiplier) }
            .filter {
                it.design.length <= design.length && design.substring(0, it.design.length) == it.design
            }.forEach(queue::enqueue)

        queue.merge(
            { pp -> pp.design },
            { p -> PatternPath(p.value[0].steps, p.value[0].design, p.value.sumOf { it.multiplier }) }
        )
        queue.sortQueue(compareBy { it.design.length })
    }

    data class PatternPath(
        val steps: List<String> = emptyList(),
        val design: String = "",
        val multiplier: Long = 1
    )

    private fun parseInput(input: List<String>): Pair<List<String>, List<String>> {
        return Pair(input[0].split(", "), input.drop(2))
    }
}