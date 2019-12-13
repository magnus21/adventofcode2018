package adventofcode.v2019

import adventofcode.util.FileParser
import adventofcode.v2019.Day13.Tile.*
import adventofcode.v2019.shared.IntCodeComputer
import kotlin.system.measureTimeMillis

object Day13 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getCommaSeparatedValuesAsList(2019, "13.txt").map { it.toLong() }

        // Run program.
        val time1 = measureTimeMillis {
            val pixels = mutableMapOf<Pixel, Int>()
            val gameState = GameState(pixels, Pixel(0, 0), Pixel(0, 0), 0)
            val result = IntCodeComputer(input.toMutableList()).runProgram().first
            updateGameState(result, gameState)
            println(gameState.pixels.values.filter { it == 2 }.count())
        }
        println("Time part 1: ($time1 milliseconds)")

        val time2 = measureTimeMillis {

            val program = input.toMutableList()
            program[0] = 2
            val computer = IntCodeComputer(program)

            val pixels = mutableMapOf<Pixel, Int>()
            val gameState = GameState(pixels, Pixel(0, 0), Pixel(0, 0), 0)
            var joystickInput = 0
            while (true) {
                val result = computer.runWithInput(listOf(joystickInput.toLong()))

                updateGameState(result.first, gameState)
                printGame(gameState)

                if (result.second == IntCodeComputer.DONE) {
                    break
                }

                joystickInput =
                        if (gameState.ball.x < gameState.paddle.x) -1 else if (gameState.ball.x > gameState.paddle.x) 1 else 0
            }


        }
        println("Time part 2: ($time2 milliseconds)")
    }

    private fun updateGameState(result: MutableList<Long>, gameState: GameState) {
        var c = 0
        while (c < result.size) {
            val pixel = Pixel(result[c].toInt(), result[c + 1].toInt())
            val tileCode = result[c + 2].toInt()

            if (pixel.x == -1 && pixel.y == 0) {
                gameState.score = tileCode
            }
            if (tileCode == PADDLE.code) {
                gameState.paddle = pixel
            } else if (tileCode == BALL.code) {
                gameState.ball = pixel
            }
            gameState.pixels[pixel] = tileCode
            c += 3
        }
    }

    private fun printGame(gameState: GameState) {
        val pixels = gameState.pixels
        val xSpan = Pair(pixels.keys.map { it.x }.min()!!, pixels.keys.map { it.x }.max()!!)
        val ySpan = Pair(pixels.keys.map { it.y }.min()!!, pixels.keys.map { it.y }.max()!!)

        val i = 0;
        for (y in ySpan.first..ySpan.second) {
            for (x in xSpan.first..xSpan.second) {
                val tile = pixels[Pixel(x, y)]

                if (tile == null) {
                    print(" ")
                } else {
                    val pixelChar = when (tile) {
                        WALL.code -> "Z"
                        BLOCK.code -> "#"
                        PADDLE.code -> "="
                        BALL.code -> "o"
                        else -> " "
                    }
                    print(pixelChar)
                }
            }
            println()
        }
        println("  Score: ${gameState.score}")
    }

    private data class Pixel(var x: Int, var y: Int) {
        override fun toString(): String {
            return "[$x, $y]"
        }
    }

    private data class GameState(
        val pixels: MutableMap<Pixel, Int>,
        var ball: Pixel,
        var paddle: Pixel,
        var score: Int? = null
    )

    enum class Tile(val code: Int) {
        EMPTY(0),
        WALL(1),
        BLOCK(2),
        PADDLE(3),
        BALL(4),

    }
}