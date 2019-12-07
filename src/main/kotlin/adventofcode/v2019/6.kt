package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.util.Queue
import kotlin.system.measureTimeMillis

object Day6 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2019, "6.txt")

        // Run program.
        val time = measureTimeMillis {
            val graphMap = parseInput(input)
            calculateLengthToCOMs(graphMap)
            //graphMap.values.forEach(::println)

            println("Total length to COM: ${getTotalLengthToCOM(graphMap)}")

            println("Length to Santa: ${getLengthFromYOUToSAN(graphMap)}")
            println("Length to Santa: ${getLengthFromYOUToSANSimplerSolution(graphMap)}")
        }
        println("Time: ($time milliseconds)")
    }


    private fun calculateLengthToCOMs(graphMap: MutableMap<String, Node>) {
        // Breadth first search
        val queue = Queue<Node>()
        queue.enqueue(graphMap["COM"]!!)
        while (!queue.isEmpty()) {
            queue.dequeue()!!.children.forEach {
                it.orbitsToCOM = it.parent!!.orbitsToCOM + 1
                if (!it.children.isEmpty()) {
                    queue.enqueue(it)
                }
            }
        }
    }

    private fun getLengthFromYOUToSANSimplerSolution(graphMap: MutableMap<String, Node>): Int {

        var node = graphMap["YOU"]!!
        val youList = mutableListOf<Node>()
        while (node.parent != null) {
            youList.add(node.parent!!)
            node = node.parent!!
        }

        node = graphMap["SAN"]!!
        val santaLista = mutableListOf<Node>()
        while (node.parent != null) {
            santaLista.add(node.parent!!)
            node = node.parent!!
        }

        val closestIntersectingNode = youList.intersect(santaLista).sortedByDescending { it.orbitsToCOM }[0]

        return youList.indexOf(closestIntersectingNode) + santaLista.indexOf(closestIntersectingNode)
    }

    private fun getLengthFromYOUToSAN(graphMap: MutableMap<String, Node>): Int {
        // Spread out search
        val visited = mutableSetOf<String>()
        val queue = Queue<Node>()
        queue.enqueue(graphMap["YOU"]!!)

        while (!queue.isEmpty()) {

            val node = queue.dequeue()!!
            visited.add(node.name)

            if (node.name.equals("SAN")) {
                println("Path: ${node.path}")
                return node.path.size - 2;
            }

            val linkedNodes = mutableListOf<Node>()
            linkedNodes.addAll(node.children)
            if (node.parent != null) {
                linkedNodes.add(node.parent!!)
            }

            linkedNodes
                .filter { !visited.contains(it.name) }
                .forEach {
                    if (!it.children.isEmpty() || it.parent != null) {
                        it.path.addAll(node.path)
                        it.path.add(node.name)
                        queue.enqueue(it)
                    }
                }
        }

        return -1
    }

    private fun getTotalLengthToCOM(graphMap: MutableMap<String, Node>): Int {
        return graphMap.values.map { it.orbitsToCOM }.sum()
    }

    private fun parseInput(input: List<String>): MutableMap<String, Node> {
        val map = mutableMapOf<String, Node>()

        for (row in input) {
            val chars = row.split(")")

            val parent = map.computeIfAbsent(chars[0]) { key -> Node(key) }
            val child = map.computeIfAbsent(chars[1]) { key -> Node(key, parent) }

            child.parent = parent
            parent.children.add(child)
        }

        return map
    }

    private data class Node(
        val name: String,
        var parent: Node? = null,
        val children: MutableList<Node> = mutableListOf(),
        var orbitsToCOM: Int = 0,
        val path: MutableList<String> = mutableListOf()
    ) {
        override fun toString(): String {
            return "Node(name=$name, orbitsToCOM=$orbitsToCOM)"
        }

        override fun equals(other: Any?): Boolean =
            if (other is Node) name == other.name else false

        override fun hashCode(): Int {
            return 31 * name.hashCode()
        }
    }
}