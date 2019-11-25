package adventofcode.v2018

import java.io.File

fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/13.txt").readLines().map { it.toCharArray() }
    val input = parseInput(rawInput)

    val carts = input.first
    val tracks = input.second

    var sortedCarts = carts.toMutableList()
    do {
        //printTrack(carts, tracks, sortedCarts)
        sortedCarts = sortedCarts.sortedWith(compareBy({ it.y }, { it.x })).toMutableList()
    } while (!tick(sortedCarts, tracks))
}

data class Cart(var x: Int, var y: Int, var dx: Int, var dy: Int, var intersectionCount: Int)
data class TrackSection(val x: Int, val y: Int, val type: Char)

fun tick(sortedCarts: MutableList<Cart>, tracks: List<List<TrackSection>>): Boolean {
    val chrashedCarts = mutableSetOf<Cart>()
    for (cart in sortedCarts) {
        cart.x += cart.dx
        cart.y += cart.dy

        val crashingCarts = crashingCarts(sortedCarts.filter { !chrashedCarts.contains(it) })
        if (crashingCarts.isNotEmpty() && !chrashedCarts.containsAll(crashingCarts)) {
            println("Crash: " + cart.x + "," + cart.y)
            chrashedCarts.addAll(crashingCarts)
            //return true
        }

        val track = tracks[cart.y][cart.x]
        if (track.type !in listOf('-', '|')) {
            updateCartDirection(cart, track.type)
        }
    }

    chrashedCarts.forEach { sortedCarts.remove(it) }

    if (sortedCarts.size == 1) {
        val lastCart = sortedCarts[0]
        println("Last cart: " + lastCart.x + "," + lastCart.y)
        return true
    }

    return false
}

fun crashingCarts(
    sortedCarts: List<Cart>
): List<Cart> {
    val crashes = sortedCarts.groupBy { it.x.toString() + "," + it.y.toString() }
        .toList()
        .sortedBy { it.second.size }
        .filter { it.second.size > 1 }

    return if (crashes.isEmpty()) emptyList() else crashes[0].second

}

fun updateCartDirection(cart: Cart, type: Char) {
    val dx = cart.dx
    val dy = cart.dy
    when (type) {
        '/' -> {
            cart.dx = -dy
            cart.dy = -dx
        }
        '\\' -> {
            cart.dx = dy
            cart.dy = dx
        }
        '+' -> {
            when (cart.intersectionCount++ % 3) {
                0 -> {
                    cart.dx = dy
                    cart.dy = -dx
                }
                2 -> {
                    cart.dx = -dy
                    cart.dy = dx
                }
            }
        }
    }
}

fun parseInput(rawInput: List<CharArray>): Pair<List<Cart>, List<List<TrackSection>>> {
    val carts = mutableListOf<Cart>()
    val tracks = mutableListOf<List<TrackSection>>()

    for (y in 0 until rawInput.size) {
        val row = mutableListOf<TrackSection>()
        for (x in 0 until rawInput[y].size) {
            val type = rawInput[y][x]

            val cartDirection = when (type) {
                '^' -> Pair(0, -1)
                'v' -> Pair(0, 1)
                '<' -> Pair(-1, 0)
                '>' -> Pair(1, 0)
                else -> null
            }
            if (cartDirection != null) {
                carts.add(Cart(x, y, cartDirection.first, cartDirection.second, 0))
            }

            val trackType = when (type) {
                in listOf('<', '>') -> '-'
                in listOf('^', 'v') -> '|'
                else -> type
            }
            row.add(TrackSection(x, y, trackType))
        }
        tracks.add(row)
    }

    return Pair(carts, tracks)
}

private fun printTrack(
    carts: List<Cart>,
    tracks: List<List<TrackSection>>,
    sortedCarts: MutableList<Cart>
) {
    println("\nCarts: $carts")
    tracks.forEach { row ->
        run {
            row.forEach {
                val cartType = getCartForPosition(it, sortedCarts)
                print(cartType ?: it.type)
            }
            println()
        }
    }
    println()
}

fun getCartForPosition(trackSection: TrackSection, carts: List<Cart>): Char? {
    val cart = carts.firstOrNull { it.x == trackSection.x && it.y == trackSection.y }

    if (cart == null) {
        return cart
    }

    val direction = Pair(cart.dx, cart.dy)

    return when (direction) {
        Pair(0, -1) -> '^'
        Pair(0, 1) -> 'v'
        Pair(-1, 0) -> '<'
        Pair(1, 0) -> '>'
        else -> {
            null
        }
    }
}
