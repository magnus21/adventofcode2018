package adventofcode


fun main(args: Array<String>) {

    val input = 209231

    val firstRecipe = Recipe(3)
    val secondRecipe = Recipe(7)

    val recipesRing = RecipesRing(firstRecipe)
    recipesRing.appendRecipe(secondRecipe)
    recipesRing.getCurrentRecipes().addAll(listOf(firstRecipe,secondRecipe))


    var recipeCount = 2
    while (recipeCount <= input + 10) {

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
    }

    recipesRing.toList().drop(input).take(10).map { it.value }.forEach{print(it)}
}

class Recipe(val value: Int) {
    var next: Recipe = this
    override fun toString(): String {
        return "$value"
    }
}

class RecipesRing(recipe: Recipe) {

    private var currentRecipes : MutableList<Recipe> = mutableListOf()
    private var firstRecipe = recipe
    private var lastRecipe = recipe

    fun getCurrentRecipes(): MutableList<Recipe> {
        return currentRecipes
    }

    fun appendRecipe(recipe: Recipe) {
        lastRecipe.next = recipe
        lastRecipe = recipe
        lastRecipe.next = firstRecipe
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
}