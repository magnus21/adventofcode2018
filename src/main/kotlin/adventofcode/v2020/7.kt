package v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day7 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "7.txt")

        // Run program.
        val time = measureTimeMillis {
            val bags = parseInput(input)

            bags.values.forEach(::println)

            println("Part 1: ${bags.values.map { findShinyGoldBag(it, bags) }.filter { it }.count()}")
            println("Part 2: ${getBagsForOneShinyGold(bags)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun findShinyGoldBag(bag: Bag, bags: MutableMap<String, Bag>): Boolean {
        return when {
            bags[bag.name]!!.children.contains("shiny gold") -> true
            else -> bags[bag.name]!!.children.map { findShinyGoldBag(it.value, bags) }.contains(true)
        }
    }


    private fun getBagsForOneShinyGold(nodes: MutableMap<String, Bag>): Int {
        val root = nodes["shiny gold"]!!
        return getBagsForChildren(root, nodes) - 1 // remove root bag from count
    }

    private fun getBagsForChildren(
        bag: Bag,
        bags: MutableMap<String, Bag>
    ): Int {
        val bagWithChildren = bags[bag.name]!!
        return if (bagWithChildren.children.isEmpty()) 1
        else bagWithChildren.count + bagWithChildren.children.map {
            it.value.count * getBagsForChildren(
                it.value,
                bags
            )
        }.sum()
    }


    private fun parseInput(input: List<String>): MutableMap<String, Bag> {
        val bags = mutableMapOf<String, Bag>()

        for (row in input) {
            val parts = row
                .replace(" bags", "")
                .replace(" bag", "")
                .replace(".", "")
                .split(" contain ")

            if (parts[1] == "no other") {
                bags[parts[0]] = Bag(parts[0], 1, emptyMap(), true)
            } else {
                val children = parts[1].split(",")
                    .map { it.trim() }
                    .map {
                        val nodeParts = it.split(' ')
                        val child = Bag(nodeParts[1] + " " + nodeParts[2], nodeParts[0].toInt())
                        Pair(child.name, child)
                    }.toMap()

                bags[parts[0]] = Bag(parts[0], 1, children, true)
            }
        }

        return bags
    }

    private data class Bag(
        val name: String,
        val count: Int,
        val children: Map<String, Bag> = emptyMap(),
        val isRoot: Boolean = false
    ) {
        override fun toString(): String {
            return "Node(name=$name, count=$count, isRoot=$isRoot, children=$children)"
        }
    }
}