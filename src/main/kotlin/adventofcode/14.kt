package adventofcode


fun main(args: Array<String>) {

    //Part 1: val input = 209231
    val input = "209231"

    val firstRecipe = Recipe(3)
    val secondRecipe = Recipe(7)

    val recipesRing = RecipesRing(firstRecipe)
    recipesRing.appendRecipe(secondRecipe)
    recipesRing.getCurrentRecipes().addAll(listOf(firstRecipe,secondRecipe))

    var recipeCount = 2
    // Part 1:    while (recipeCount <= input + 10) {
    var tail: Pair<Boolean,Int>
    do {
        val sum = recipesRing.getSumOfCurrentRecipes()

        if (sum < 10) {
            recipesRing.appendRecipe(Recipe(sum))
            recipeCount++
        } else {
            recipesRing.appendRecipe(Recipe(sum / 10))
            recipesRing.appendRecipe(Recipe(sum % 10))
            recipeCount+=2
        }

        val currentRecipes = recipesRing.getCurrentRecipes()
            .map {
                recipesRing.recipeStepsAway(1 + it.value, it)
            }
        recipesRing.getCurrentRecipes().clear()
        recipesRing.getCurrentRecipes().addAll(currentRecipes)


        /*
        recipesRing.toList().map {
            if (it == recipesRing.getCurrentRecipes()[0]) "($it)" else (if (it == recipesRing.getCurrentRecipes()[1]) "[$it]" else " $it ")
        }.forEach { print(it) }
        println(" : $recipeCount")
        */

        tail = recipesRing.hasTail(input, sum >= 10)
    } while(!tail.first)

    // Part 1: recipesRing.toList().drop(input).take(10).map { it.value }.forEach{print(it)}
    println("recipeCount: " + (recipeCount + tail.second - input.length))
}

class Recipe(val value: Int) {
    var next: Recipe = this
    var previous: Recipe = this
    override fun toString(): String {
        return "$value"
    }
}

class RecipesRing(recipe: Recipe) {

    private var currentRecipes : MutableList<Recipe> = mutableListOf()
    private var firstRecipe = recipe

    fun getCurrentRecipes(): MutableList<Recipe> {
        return currentRecipes
    }

    fun appendRecipe(recipe: Recipe) {
        firstRecipe.previous.next = recipe
        recipe.previous = firstRecipe.previous
        recipe.next = firstRecipe
        firstRecipe.previous = recipe
    }

    fun recipeStepsAway(steps: Int, recipe: Recipe): Recipe {
        var newRecipe = recipe
        for (step in 1..steps) {
            newRecipe = newRecipe.next
        }
        return newRecipe
    }

    fun toList(): List<Recipe> {
        val list = mutableListOf<Recipe>()

        var recipe = firstRecipe
        do {
            list.add(recipe)
            recipe = recipe.next
        } while (recipe != firstRecipe)

        return list
    }

    fun getSumOfCurrentRecipes(): Int {
        return currentRecipes.map { it.value }.sum()
    }

    fun hasTail(input: String, checkTwoLast: Boolean): Pair<Boolean,Int> {
        var recipe = firstRecipe.previous
        val tail1 = mutableListOf<Recipe>()
        for (i in 1..input.length) {
            tail1.add(recipe)
            recipe = recipe.previous
        }
        val last = Pair(tail1.reversed().joinToString("") == input, 0)

        if(last.first) {
            return last
        }

        if(checkTwoLast) {
            val tail2 = mutableListOf<Recipe>()
            for (i in 1..input.length) {
                tail2.add(recipe)
                recipe = recipe.next
            }

            return  Pair(tail2.joinToString("") == input, -1)
        }

        return last
    }
}