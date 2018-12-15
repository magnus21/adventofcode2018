package adventofcode

import adventofcode.Type.ELF
import adventofcode.Type.GOBLIN
import java.io.File


fun main(args: Array<String>) {

    val input = File("src/main/resources/15.txt").readLines()

    val (fieldSize, wallFieldPoints, fighters) = parseGame(input)

    println(fighters)
    printGame(fieldSize, wallFieldPoints, fighters)

    var combatOver = false
    var roundCount = 0
    do {
        if (fighters.filter { it.hitPoints > 0 }.groupBy { it.type }.size == 1) {
            combatOver = true
        } else {
            for (fighter in fighters.filter { it.hitPoints > 0 }.toMutableList()) {
                if (!fighters.filter { it.hitPoints > 0 }.filter { it.type != fighter.type }.any()) {
                    combatOver = true
                    roundCount--
                    break
                }
                if (fighter.hitPoints > 0) {
                    doTurnFor(fighter, fighters.filter { it.hitPoints > 0 }.toMutableList(), wallFieldPoints)
                }
            }
            roundCount++
        }

        // Sort fighter after each round.
        fighters.sortWith(compareBy({ it.position.y }, { it.position.x }))

        println("\nAfter $roundCount rounds:")
        printGame(fieldSize, wallFieldPoints, fighters)

    } while (!combatOver)

    val hitPointsLeft = fighters.filter { it.hitPoints > 0 }.map { it.hitPoints }.sum()

    println("$roundCount * $hitPointsLeft: " + roundCount * hitPointsLeft)
}

fun doTurnFor(
    fighter: Fighter,
    sortedFighters: MutableList<Fighter>,
    wallFieldPoints: MutableSet<FieldPoint>
) {
    val enemyType = if (fighter.type == GOBLIN) ELF else GOBLIN
    val enemyPaths = mutableSetOf<Pair<Fighter, List<FieldPoint>>>()
    findClosestEnemies(
        enemyType,
        fighter,
        wallFieldPoints,
        sortedFighters,
        mutableListOf(),
        enemyPaths,
        fighter.position,
        mutableMapOf()
    )

    val neighbourEnemies = enemyPaths.filter { it.second.isEmpty() }.map { it.first }
    if (neighbourEnemies.isNotEmpty()) {
        // Sort by hit count and position
        val chosenEnemy = neighbourEnemies
            .sortedWith(compareBy({ it.hitPoints }, { it.position.y }, { it.position.x }))
            .first()

        // Attack
        debugPrint(fighter, chosenEnemy, "ATTACK DIRECTLY", "attacks")
        attackEnemy(fighter.attackPower, chosenEnemy)
    } else if (enemyPaths.isNotEmpty()) {
        // Move towards enemy
        val chosenEnemyPair = enemyPaths
            .sortedWith(compareBy({ it.second.size }, { it.first.position.y }, { it.first.position.x }))
            .first()

        val chosenEnemy = chosenEnemyPair.first

        println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " moves toward " + chosenEnemy.type.toString() + ":" + chosenEnemy.position.x + "," + chosenEnemy.position.y)

        // Move
        fighter.position.x = chosenEnemyPair.second.first().x
        fighter.position.y = chosenEnemyPair.second.first().y

        if (chosenEnemyPair.second.size == 1) {
            // Attack after move
            debugPrint(fighter, chosenEnemy, "ATTACK AFTER MOVE", "attacks")
            attackEnemy(fighter.attackPower, chosenEnemy)
        }

        if (chosenEnemy.hitPoints <= 0) {
            debugPrint(fighter, chosenEnemy, "KILL", "killed")
        }
    }
}

fun attackEnemy(attackPower: Int, enemy: Fighter?) {
    enemy!!.hitPoints -= attackPower
}

fun findClosestEnemies(
    enemyType: Type,
    fighter: Fighter,
    wallFieldPoints: MutableSet<FieldPoint>,
    sortedFighters: MutableList<Fighter>,
    previousPositions: MutableList<FieldPoint>,
    enemyPaths: MutableSet<Pair<Fighter, List<FieldPoint>>>,
    pos: FieldPoint,
    reachedPositions: MutableMap<FieldPoint, Int>
) {
    if (enemyPaths.isEmpty() || previousPositions.size <= enemyPaths.first().second.size) {
        var enemiesFoundOnCurrentDistance = false
        val newPositions = mutableListOf<FieldPoint>()

        listOf(Pair(0, -1), Pair(-1, 0), Pair(1, 0), Pair(0, 1))
            .forEach { direction ->
                val newPos = FieldPoint(pos.x + direction.first, pos.y + direction.second)

                reachedPositions.getOrDefault(newPos, Int.MAX_VALUE)
                if (previousPositions.size <= reachedPositions.getOrDefault(newPos, Int.MAX_VALUE)) {

                    val otherFighter =
                        sortedFighters.firstOrNull { f -> f.position.x == newPos.x && f.position.y == newPos.y }

                    if (otherFighter != null && otherFighter.type == enemyType) {
                        // Enemy found
                        enemiesFoundOnCurrentDistance = true
                        val previousPositionsList = mutableListOf<FieldPoint>()
                        previousPositionsList.addAll(previousPositions)

                        enemyPaths.add(Pair(otherFighter, previousPositionsList))
                        //println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " found enemy:" + "  newPos: $newPos")
                    } else if (!wallFieldPoints.contains(newPos) && otherFighter == null) {
                        // Dot position found
                        newPositions.add(newPos)
                        reachedPositions.put(newPos, previousPositions.size)
                        //println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + "  newPos: $newPos, previousPositions: " + previousPositions)
                    }
                }
            }

        if (!enemiesFoundOnCurrentDistance && newPositions.isNotEmpty()) {
            for (newPos in newPositions) {
                val newPreviousPositions = mutableListOf<FieldPoint>()
                newPreviousPositions.addAll(previousPositions)
                newPreviousPositions.add(newPos)

                findClosestEnemies(
                    enemyType,
                    fighter,
                    wallFieldPoints,
                    sortedFighters,
                    newPreviousPositions,
                    enemyPaths,
                    newPos,
                    reachedPositions
                )
            }
        }
    }
}

fun parseGame(input: List<String>): Triple<Pair<Int, Int>, MutableSet<FieldPoint>, MutableList<Fighter>> {

    val fieldSize = Pair(input[0].length, input.size)

    val fieldPoints = mutableSetOf<FieldPoint>()
    val fighters = mutableListOf<Fighter>()

    for (y in 0 until fieldSize.second) {
        val row = input[y]
        for (x in 0 until fieldSize.first) {
            when (row[x]) {
                '#' -> fieldPoints.add(FieldPoint(x, y))
                'G' -> fighters.add(Fighter(FieldPoint(x, y), GOBLIN))
                'E' -> fighters.add(Fighter(FieldPoint(x, y), ELF))
            }
        }
    }

    return Triple(fieldSize, fieldPoints, fighters)
}

data class FieldPoint(var x: Int, var y: Int)
data class Fighter(var position: FieldPoint, val type: Type, var hitPoints: Int = 200, var attackPower: Int = 3)
enum class Type(val code: String) {
    GOBLIN("G"), ELF("E"), WALL("#"), DOT(".")
}

fun printGame(fieldSize: Pair<Int, Int>, wallFieldPoints: MutableSet<FieldPoint>, fighters: MutableList<Fighter>) {
    for (y in 0 until fieldSize.second) {
        for (x in 0 until fieldSize.first) {
            val fighter = fighters.filter { it.hitPoints > 0 }.firstOrNull { it.position.x == x && it.position.y == y }
            if (wallFieldPoints.contains(FieldPoint(x, y))) {
                print('#')
            } else if (fighter != null) {
                print(fighter.type.code)
            } else {
                print(".")
            }
        }
        println("  " +
                fighters
                    .filter { it.hitPoints > 0 }
                    .filter { it.position.y == y }
                    .sortedWith(compareBy({ it.position.y }, { it.position.x }))
                    .map { "" + it.type.code + "(" + it.hitPoints + ")" }
                    .joinToString(", ")
        )
    }
    println()
}

fun debugPrint(fighter: Fighter, chosenEnemy: Fighter, prefix: String, message: String) {
    //println(prefix + ": " + fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " - (" + fighter.hitPoints + ") " + message + " " + chosenEnemy.type.toString() + ":" + chosenEnemy.position.x + "," + chosenEnemy.position.y + " - (" + fighter.hitPoints + ") ")
}
