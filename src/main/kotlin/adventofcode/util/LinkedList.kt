package adventofcode.util

class LinkedList<T>(private val head: Node<T>) {

    private var current: Node<T> = head

    private val lookupMap = mutableMapOf<T, Node<T>>()

    /** Using this we only work if added nodes values are never changed or removed (remove and insert back works!) */
    fun generateLookupMap(length: Int? = null) {

        var node = head
        var len = length ?: -1
        do {
            lookupMap[node.value] = node
            node = node.next!!
            len--
        } while (node != head && node.next != null && len != 0)
    }

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

    fun insert(value: T): Node<T> {
        return insertNode(Node(value))
    }

    fun insertNode(node: Node<T>): Node<T> {
        val tmp = current
        val tmpNext = current.next!!
        current = node

        node.previous = tmp
        tmp.next = node

        tmpNext.previous = node
        node.next = tmpNext

        return node
    }

    fun removeNext(): Node<T> {
        stepToNext()
        val node = current
        current = node
        node.previous!!.next = node.next
        node.next!!.previous = node.previous

        if (current == head) {
            head == node.next!!
        }
        current = node.previous!!


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
        list.add(head.value)
        var node = head.next

        var len = length ?: -1
        while (node != head && node != null && len != 0) {
            list.add(node.value)
            if(node.next == null) {
                break
            }
            node = node.next!!
            len--
        }

        return list
    }

    fun toListStartAtCurrent(length: Int? = null): List<T> {
        val list = mutableListOf<T>()

        var node = current
        var len = length ?: -1
        do {
            list.add(node.value)
            node = node.next!!
            len--
        } while (node != current && node.next != null && len != 0)

        return list
    }

    /** NB! Can only be used if lookupMap has been generated */
    fun goto(value: T): Node<T>? {
        val result = lookupMap[value]
        if (result != null) {
            current = result
            return current
        }
        return null
    }
}

class Node<T>(val value: T) {
    var next: Node<T>? = null
    var previous: Node<T>? = null
    override fun toString(): String {
        return "$value"
    }
}
