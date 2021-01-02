package adventofcode.v2020

import adventofcode.util.AdventOfCodeUtil
import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day21 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "21.txt")
        val foods = parseInput(input)

        val time = measureTimeMillis {
            val (possibleMatches, result1) = part1(foods)
            println("Part 1: $result1")
            println("Part 2: ${part2(possibleMatches)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(foods: List<Food>): Pair<List<Pair<String, List<String>>>, Int> {

        val allAllergens = foods.flatMap { it.allergens }.distinct()

        val possibleMatches = allAllergens.map { allergen ->
            val matchingFoods = foods.filter { it.allergens.contains(allergen) }
            val allMatchingIngredients = matchingFoods.flatMap { it.ingredients }.distinct()

            val matchingIngredients = allMatchingIngredients.filter { ingredient ->
                matchingFoods.all { it.ingredients.contains(ingredient) }
            }

            Pair(allergen, matchingIngredients)
        }
        val ingredientsWithAllergen = possibleMatches.flatMap { it.second }.distinct()

        return Pair(
            possibleMatches,
            foods.map { food -> food.ingredients.count { !ingredientsWithAllergen.contains(it) } }.sum()
        )
    }

    private fun part2(possibleMatches: List<Pair<String, List<String>>>): String {
        return AdventOfCodeUtil.reduceOneToManyMatches(possibleMatches)
            .toList()
            .sortedBy { it.first }
            .joinToString(",") { it.second }
    }

    private fun parseInput(input: List<String>): List<Food> {

        return input.map {
            val parts = it.split("(")
            Food(parts[0].trim().split(" "), parts[1].trim().replace(")", "").replace("contains ", "").split(", "))
        }
    }

    data class Food(
        val ingredients: List<String>,
        val allergens: List<String>
    )
}