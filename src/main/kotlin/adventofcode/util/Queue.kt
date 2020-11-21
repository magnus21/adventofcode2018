package adventofcode.util

class Queue<T> {
    private val items: MutableList<T> = mutableListOf()

    fun enqueue(item: T) {
        items.add(item)
    }

    fun dequeue(): T? {
        return if (this.isEmpty()) {
            null
        } else {
            items.removeAt(0)
        }
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return items.isNotEmpty()
    }

    fun toList(): MutableList<T> {
        return items.toMutableList()
    }

    fun contains(t: T): Boolean {
        return items.contains(t)
    }

    fun sortQueue(comparator: Comparator<T>) {
        items.sortWith(comparator)
    }
}