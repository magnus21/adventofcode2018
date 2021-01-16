package adventofcode.v2015

import adventofcode.util.FileParser
import kotlinx.serialization.json.*
import kotlin.system.measureTimeMillis

object Day12 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2015, "12.txt")[0]

        val time1 = measureTimeMillis {
            val answer = Regex("[-]?[\\d]+").findAll(input).map { it.value.toInt() }.sum()
            println("Part 1: $answer ")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            println("Part 2: ${getSum(Json.parseToJsonElement(input))}")
        }
        println("Time: $time2 ms")
    }

    private fun getSum(json: JsonElement): Int {

        if (json is JsonObject) {
            val hasRed = json.jsonObject.entries.any { e ->
                if (e.value is JsonPrimitive) {
                    e.value.jsonPrimitive.content == "red"
                } else false
            }

            if (hasRed) {
                return 0
            }
        }

        val regex = "[-]?[\\d]+".toRegex()

        return if (json is JsonArray) {
            json.map { e -> evaluateJsonEntry(e, regex) }.sum()
        } else {
            json.jsonObject.entries.map { e -> evaluateJsonEntry(e.value, regex) }.sum()
        }
    }

    private fun evaluateJsonEntry(je: JsonElement, regex: Regex) =
        if (je is JsonPrimitive) regex.find(je.jsonPrimitive.content)?.value?.toInt()
            ?: 0 else getSum(je)
}