package adventofcode.util

import java.util.function.Function

class Queue<T : Any> {
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

    fun <K> merge(
        groupFunction: Function<T, K>,
        mergeFunction: Function<Map.Entry<K, List<T>>, T>
    ) {
        val merged = items
            .groupBy { groupFunction.apply(it) }
            .map { mergeFunction.apply(it) }

        items.clear()
        items.addAll(merged)
    }
}