package adventofcode.v2018

import java.io.File

fun main(args: Array<String>) {

    val nodes = File("src/main/resources/8.txt").readText().split(" ").map { Integer.valueOf(it) }

    //println(nodes) 36676
    /*val result = mutableListOf<Int>()
    var rest = nodes
    do {
        rest = getMetaDataEntries(mutableListOf(), rest, result)
    } while (rest.isNotEmpty())

    println(result.sum())
    */

    val rootNode = Node(1, 0, mutableListOf(), mutableListOf())
    buildTree(nodes, rootNode)

    println(getTotalMetadataEntriesSum(rootNode.children[0]))
    println(getValueOf(rootNode.children[0]))
}

fun buildTree(
    nodes: List<Int>,
    parent: Node
): List<Int> {
    val nrOfChildren = nodes[0]
    val nrOfMetaDataEntries = nodes[1]

    val node = Node(nrOfChildren, nrOfMetaDataEntries, mutableListOf(), mutableListOf())

    if (nrOfChildren > 0) {
        var childrenLeft = nodes.drop(2)
        do {
            childrenLeft = buildTree(childrenLeft, node)
        } while (node.nrOfChildren > node.children.size)

        node.metadataEntries.addAll(childrenLeft.take(nrOfMetaDataEntries))
        parent.children.add(node)

        return childrenLeft.drop(nrOfMetaDataEntries)
    } else {
        node.metadataEntries.addAll(nodes.drop(2).take(nrOfMetaDataEntries))
        parent.children.add(node)

        return nodes.drop(2 + nrOfMetaDataEntries)
    }
}

fun getValueOf(node: Node): Int {
    if(node.children.size == 0) {
        return node.metadataEntries.sum()
    }
    return node.metadataEntries
        .filter { it <= node.children.size }
        .map { getValueOf(node.children[it - 1]) }
        .sum()
}

fun getTotalMetadataEntriesSum(node: Node) : Int {
    val nodeSum = node.metadataEntries.sum()
    if(node.children.size > 0) {
        return nodeSum + node.children.map { getTotalMetadataEntriesSum(it) }.sum()
    } else {
        return nodeSum
    }
}

/*
fun getMetaDataEntries(
    rest: MutableList<Int>,
    nodes: List<Int>,
    result: MutableList<Int>
): List<Int> {
    val nrOfChildren = nodes[0]
    val nrOfMetaDataEntries = nodes[1]

    if (nrOfChildren > 0) {
        rest.addAll(nodes.take(2))
        return getMetaDataEntries(rest, nodes.drop(2), result)
    }

    result.addAll(nodes.drop(2).take(nrOfMetaDataEntries))

    if (rest.isNotEmpty()) {
        rest[rest.size - 2]--
    }

    return rest + nodes.drop(2 + nrOfMetaDataEntries)
}
*/

data class Node(
    var nrOfChildren: Int,
    val nrOMetadataEntries: Int,
    val children: MutableList<Node>,
    val metadataEntries: MutableList<Int>
)
/*
3 3 0 3 10 11 12 2 1 0 1 99 0 1 98 2 0 2 5 5 1 1 2
A-------------------------------------------------
    B----------- C-------------------X------
                     D------E-----
*/
