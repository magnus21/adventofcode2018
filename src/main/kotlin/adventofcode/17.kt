package adventofcode

import adventofcode.SquareState.*
import adventofcode.util.Queue
import java.io.File

fun main(args: Array<String>) {

    val rawInput = File("src/main/resources/17.txt").readLines()
    val (boundaries, squares) = parseSquares(rawInput)

    val lowestY = squares.filter { it.value == CLAY }.keys.sortedBy { it.y }.first().y
    val springQueue = Queue<Square>()
    springQueue.enqueue(Square(500, Math.max(0, lowestY - 1)))

    printSquares(boundaries, squares, true, springQueue)

    while (!springQueue.isEmpty()) {
        tapSpring(squares, springQueue)
        //printSquares(boundaries, squares, true, springQueue)
    }

    printSquares(boundaries, squares, false, springQueue)

    val answerPartOne =
        squares.filter { it.key.y >= lowestY }.filter { it.value == WATER_PASSED || it.value == WATER }.count()

    println(answerPartOne)

    val answerPartTwo = squares.filter { it.key.y >= lowestY }.filter { it.value == WATER }.count()
    println(answerPartTwo)
}

data class Square(var x: Int, var y: Int)
enum class SquareState(val code: String) {
    CLAY("#"), WATER_PASSED("|"), WATER("~"), SAND(".")
}

fun tapSpring(
    squares: MutableMap<Square, SquareState>,
    springQueue: Queue<Square>
) {
    val movingWater = springQueue.dequeue()!!

    do {
        val nextSquare = Square(movingWater.x, movingWater.y + 1)
        val nextSquareState = squares[nextSquare]

        if (nextSquareState == null) {
            return
        } else if (nextSquareState == SAND) {
            squares.put(nextSquare, WATER_PASSED)
        } else if (nextSquareState == CLAY) {
            // Explore level
            exploreLevel(movingWater, squares, springQueue)
            break
        }

        movingWater.y++
    } while (true)
}

fun exploreLevel(
    startSquare: Square,
    squares: MutableMap<Square, SquareState>,
    springQueue: Queue<Square>
) {
    // Find  boundaries
    val leftEdge = findClosestClayOrDrop(-1, startSquare, squares)
    val rightEdge = findClosestClayOrDrop(1, startSquare, squares)

    if (leftEdge != null && leftEdge.first == CLAY && rightEdge != null && rightEdge.first == CLAY) {
        // Fill level with water
        for (x in leftEdge.second.x..rightEdge.second.x) {
            squares.put(Square(x, startSquare.y), WATER)
        }
        // Explore above level
        exploreLevel(
            Square(startSquare.x, startSquare.y - 1),
            squares,
            springQueue
        )
    } else {
        // Add "new" springs.
        if (leftEdge != null && leftEdge.first == SAND) {
            if (!springQueue.contains(leftEdge.second)) {
                springQueue.enqueue(leftEdge.second)
            }
        }
        if (rightEdge != null && rightEdge.first == SAND) {
            if (!springQueue.contains(rightEdge.second)) {
                springQueue.enqueue(rightEdge.second)
            }
        }
    }
}

fun findClosestClayOrDrop(
    direction: Int,
    square: Square,
    squares: MutableMap<Square, SquareState>
): Pair<SquareState, Square>? {
    val nextSquare = Square(square.x + direction, square.y)
    val nextBottomSquare = Square(square.x + direction, square.y + 1)

    val nextSquareState = squares.get(nextSquare)
    val nextBottomSquareState = squares.get(nextBottomSquare)

    if (nextSquareState == null) {
        return null
    }

    squares.put(square, WATER_PASSED)

    if (nextBottomSquareState == WATER_PASSED) {
        return null
    } else if (nextSquareState == CLAY) {
        return Pair(CLAY, square)
    } else if (nextBottomSquareState == SAND) {
        squares.put(nextSquare, WATER_PASSED)
        return Pair(SAND, nextSquare)
    }

    return findClosestClayOrDrop(direction, nextSquare, squares)
}

fun parseSquares(rawInput: List<String>): Pair<Boundaries, MutableMap<Square, SquareState>> {
    val squares = mutableMapOf<Square, SquareState>()

    rawInput.forEach {
        val mainParts = it.split(", ")

        if (mainParts[0].startsWith("x")) {
            val x = Integer.valueOf(mainParts[0].split("=")[1])
            val ys = mainParts[1].split("=")[1].split("..").map { Integer.valueOf(it) }
            for (y in ys[0]..ys[1]) {
                squares.put(Square(x, y), CLAY)
            }
        } else {
            val y = Integer.valueOf(mainParts[0].split("=")[1])
            val xs = mainParts[1].split("=")[1].split("..").map { Integer.valueOf(it) }
            for (x in xs[0]..xs[1]) {
                squares.put(Square(x, y), CLAY)
            }
        }
    }

    val boundaries = getBoundaries(squares.keys)

    // Pad with sand
    for (y in 0..boundaries.ymax) {
        for (x in (boundaries.xmin - 1)..(boundaries.xmax + 1)) {
            if (squares.get(Square(x, y)) == null) {
                squares.put(Square(x, y), SAND)
            }
        }
    }

    return Pair(boundaries, squares)
}


fun getBoundaries(squares: Set<Square>): Boundaries {
    return squares.fold(Boundaries(10000, 100000, -1, -1)) { bounds, square ->
        if (square.x < bounds.xmin)
            bounds.xmin = square.x
        if (square.y < bounds.ymin)
            bounds.ymin = square.y
        if (square.x > bounds.xmax)
            bounds.xmax = square.x
        if (square.y > bounds.ymax)
            bounds.ymax = square.y
        bounds
    }
}

private fun printSquares(
    boundaries: Boundaries,
    squares: MutableMap<Square, SquareState>,
    onlyPrintAffectedPart: Boolean,
    springQueue: Queue<Square>
) {

    val levelProgress = squares
        .filter { it.value == WATER_PASSED || it.value == WATER }
        .map { it.key }
        .sortedByDescending { it.y }
        .map { it.y }
        .firstOrNull()

    println(
        "xmin: " + (boundaries.xmin - 1) + ", xmax: " + (boundaries.xmax + 1) + " levelProgress: " + levelProgress
            ?: "-"
    )
    print("Springs: ")
    springQueue.toList().forEach { print("(" + it.x + ", " + it.y + ")") }
    println()

    val yStart = if (levelProgress == null || !onlyPrintAffectedPart) 0 else Math.max(0, levelProgress - 50)

    for (y in yStart..(boundaries.ymax)) {
        print(y.toString().padStart(2, ' ').padEnd(3, ' '))
        for (x in (boundaries.xmin - 1)..(boundaries.xmax + 1)) {
            if (y == 0 && x == 500) {
                print('+')
            } else {
                print(squares.getOrDefault(Square(x, y), SAND).code)
            }
        }
        println()
        if (levelProgress != null && y > levelProgress + 2 && onlyPrintAffectedPart) {
            break
        }
    }

    println()
}