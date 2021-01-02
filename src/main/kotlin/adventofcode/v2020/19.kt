package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day19 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "19.txt")
        val (rules, messages) = parseInput(input)

        val time = measureTimeMillis {
            println("Part 1: ${part1(rules, messages)}")
            println("Part 2: ${part2(rules, messages)}")
        }
        println("Time: ($time milliseconds)")
    }

    data class Rule(
        val index: Int,
        val value: String?,
        val rule1: List<Int> = listOf(),
        val rule2: List<Int> = listOf()
    )

    private fun part1(rules: Map<Int, Rule>, messages: List<String>): Int {

        val regex = getRegEx(0, rules).toRegex()

        return messages.filter { m -> regex.matches(m) }.count()
    }

    private fun part2(rules: Map<Int, Rule>, messages: List<String>): Int {

        val regex = getRegExPart2(0, rules).toRegex()

        return messages.filter { m -> regex.matches(m) }.count()
    }


    private fun getRegEx(
        ruleKey: Int,
        rules: Map<Int, Rule>
    ): String {
        val rule = rules[ruleKey] ?: error("rule not found")

        if (rule.value != null) {
            return rule.value
        }

        if (rule.rule2.isNotEmpty()) {
            return "(" +
                    rule.rule1.joinToString("") { getRegEx(it, rules) } +
                    "|" +
                    rule.rule2.joinToString("") { getRegEx(it, rules) } +
                    ")"
        }

        return rule.rule1.joinToString("") { getRegEx(it, rules) }
    }


    private fun getRegExPart2(
        ruleKey: Int,
        rules: Map<Int, Rule>
    ): String {
        val rule = rules[ruleKey] ?: error("rule not found")

        // 8 are just repeating isself ie (42|42 42|42 42 42|..)
        if (ruleKey == 8) {
            return "(" + getRegExPart2(42, rules) + "{1,})"
        }
        // 11 is expanding the pair 42,31 ie, (42 31|42 42 31 31|42 42 42 31 31 31|.. )
        if (ruleKey == 11) {
            // Brut force regex rule.. 10 should be enough..
            return "(" + (1..10).joinToString("|") {
                "(" + getRegExPart2(42, rules) + "{$it}" + getRegEx(
                    31,
                    rules
                ) + "{$it})"
            } + ")"
        }

        if (rule.value != null) {
            return rule.value
        }

        if (rule.rule2.isNotEmpty()) {
            return "(" +
                    rule.rule1.joinToString("") { getRegExPart2(it, rules) } +
                    "|" +
                    rule.rule2.joinToString("") { getRegExPart2(it, rules) } +
                    ")"
        }

        return rule.rule1.joinToString("") { getRegExPart2(it, rules) }
    }

    private fun parseInput(input: List<String>): Pair<Map<Int, Rule>, List<String>> {

        return Pair(
            input.takeWhile { it != "" }
                .map {
                    val parts = it.split(":")
                    when {
                        parts[1].contains("\"") -> Rule(parts[0].toInt(), parts[1].trim().replace("\"", ""))
                        !parts[1].contains("\"") && !parts[1].contains("|") -> Rule(
                            parts[0].toInt(),
                            null,
                            parts[1].trim().split(" ").map(String::toInt)
                        )
                        else -> {
                            val subParts = parts[1].trim().split("|")
                            Rule(
                                parts[0].toInt(),
                                null,
                                subParts[0].trim().split(" ").map(String::toInt),
                                subParts[1].trim().split(" ").map(String::toInt)
                            )
                        }
                    }
                }.sortedBy { it.index }
                .map { Pair(it.index, it) }
                .toMap(),
            input.dropWhile { it != "" }
                .filter { it != "" }
        )
    }
}