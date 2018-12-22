package adventofcode.util

class Queue<T> {
    val items: MutableList<T> = mutableListOf()

    fun enqueue(item: T) {
        items.add(item)
    }

    fun dequeue():T?{
        if (this.isEmpty()){
            return null
        } else {
            return items.removeAt(0)
        }
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun toList(): MutableList<T> {
        return items.toMutableList()
    }

    fun contains(t: T): Boolean {
        return items.contains(t)
    }
}