package adventofcode.v2019

import adventofcode.util.FileParser

object Day8 {

    @JvmStatic
    fun main(args: Array<String>) {

        val pixels = FileParser.getFileRows(2019, "8.txt").fold("") { a, b -> a + b }.map(Character::getNumericValue)

        val length = 25
        val height = 6
        val layerSize = length * height
        val layers = pixels.chunked(layerSize)

        // Part 1
        val layerDigitsCountMap = layers
            .map { it.groupBy { i -> i } }

        val fewestZerosLayer = layerDigitsCountMap.filter { it.containsKey(0) }.sortedBy { it[0]!!.size }[0]
        println(fewestZerosLayer[1]!!.size * fewestZerosLayer[2]!!.size)


        // Part 2 (0 = black, 1 = white, 2 = transparent)
        val picturePixels = Array(layerSize) { 2 }
        for (layer in layers) {
            for (i in 0 until layerSize) {
                if (picturePixels[i] == 2) {
                    picturePixels[i] = layer[i]
                }
            }
        }

        // Invert colors since I have white background..
        for (i in 0 until layerSize) {
            print(if (picturePixels[i] == 0) " " else "*")
            if ((i + 1) % length == 0) {
                println()
            }
        }

    }
}