package adventofcode.util

class LinkedList<T>(private val head: Node<T>) {

    private var current: Node<T> = head

    companion object {
        fun <T> fromList(list: List<T>): LinkedList<T> {
            val first = Node(list[0])
            val linkedList = LinkedList(first)
            list.drop(1).forEach { linkedList.append(it) }
            return linkedList
        }
    }

    fun startFromHead() {
        current = head
    }

    fun connectCurrentToHead() {
        current.next = head
        head.previous = current
    }

    fun append(value: T): Node<T> {
        val node = Node(value)
        val tmp = current
        current = node
        node.previous = tmp
        tmp.next = node
        return node
    }

    fun getCurrent(): Node<T> {
        return current
    }

    fun stepToNext() {
        current = current.next!!
    }


    fun nextNodeStepsAway(): Node<T> {
        return nodeStepsAway(1, true)
    }

    fun previousMarbleStepsAway(): Node<T> {
        return nodeStepsAway(-1, false)
    }

    fun nextNodeStepsAway(steps: Int): Node<T> {
        return nodeStepsAway(steps, true)
    }

    fun previousMarbleStepsAway(steps: Int): Node<T> {
        return nodeStepsAway(steps, false)
    }

    private fun nodeStepsAway(steps: Int, next: Boolean): Node<T> {
        var node = current
        for (step in 1..steps) {
            node = if (next) node.next!! else node.previous!!
        }
        return node
    }

    fun toList(length: Int? = null): List<T> {
        val list = mutableListOf<T>()

        var node = head
        var len = length ?: -1
        do {
            list.add(node.value)
            node = node.next!!
            len--
        } while (node != head && node.next != null && len != 0)

        return list
    }
}

class Node<T>(val value: T) {
    var next: Node<T>? = null
    var previous: Node<T>? = null
    override fun toString(): String {
        return "$value"
    }
}
