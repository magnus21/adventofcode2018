package adventofcode

import adventofcode.Type.ELF
import adventofcode.Type.GOBLIN
import java.io.File


fun main(args: Array<String>) {

    val input = File("src/main/resources/15-test.txt").readLines()

    val (fieldSize, wallFieldPoints, fighters) = parseGame(input)

    println(fighters)
    printGame(fieldSize, wallFieldPoints, fighters)

    var combatOver = false
    var roundCount = 0
    do {
        for (fighter in fighters.filter { it.hitCount > 0 }.toMutableList()) {
            if (!fighters.filter { it.hitCount > 0 }.filter { it.type != fighter.type }.any()) {
                combatOver = true
                break
            }
            if (fighter.hitCount > 0) {
                doTurnFor(fighter, fighters.filter { it.hitCount > 0 }.toMutableList(), wallFieldPoints)
            }
        }

        // Sort fighter after each round.
        fighters.sortWith(compareBy({ it.position.y }, { it.position.x }))

        roundCount++
    } while (!combatOver)

    printGame(fieldSize, wallFieldPoints, fighters)

    val hitCountsLeft = fighters.filter { it.hitCount > 0 }.map { it.hitCount }.sum()

    println("$roundCount * $hitCountsLeft: " + roundCount * hitCountsLeft)
}

fun doTurnFor(
    fighter: Fighter,
    sortedFighters: MutableList<Fighter>,
    wallFieldPoints: MutableSet<FieldPoint>
) {
    val enemyType = if (fighter.type == GOBLIN) ELF else GOBLIN
    val enemies = mutableSetOf<Pair<Fighter, List<FieldPoint>>>()
    findClosestEnemies(
        enemyType,
        fighter,
        wallFieldPoints,
        sortedFighters,
        mutableListOf(),
        enemies,
        fighter.position,
        mutableSetOf()
    )

    val neighbourEnemies = enemies.filter { it.second.size == 0 }.map { it.first }
    if (neighbourEnemies.isNotEmpty()) {
        // Sort by hit count and position
        val chosenEnemy = neighbourEnemies
            .sortedWith(compareBy({ it.hitCount }, { it.position.y }, { it.position.x }))
            .first()

        // Attack
        debugPrint(fighter, chosenEnemy, "ATTACK", "attacks")
        attackEnemy(fighter.attackPower, chosenEnemy)

        if (chosenEnemy.hitCount <= 0) {
            debugPrint(fighter, chosenEnemy, "KILL", "killed")
        }
    } else if (enemies.isNotEmpty()) {
        // Move towards enemy
        val chosenEnemyPair = enemies
            .sortedWith(compareBy({ it.second.size }, { it.first.position.y }, { it.first.position.x }))
            .first()

        val chosenEnemy = chosenEnemyPair.first

        println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " moves toward " + chosenEnemy.type.toString() + ":" + chosenEnemy.position.x + "," + chosenEnemy.position.y)

        // Move
        fighter.position.x = chosenEnemyPair.second.first().x
        fighter.position.y = chosenEnemyPair.second.first().y
    }
}

fun attackEnemy(attackPower: Int, enemy: Fighter?) {
    enemy!!.hitCount -= attackPower
}

fun findClosestEnemies(
    enemyType: Type,
    fighter: Fighter,
    wallFieldPoints: MutableSet<FieldPoint>,
    sortedFighters: MutableList<Fighter>,
    previousPositions: MutableList<FieldPoint>,
    enemies: MutableSet<Pair<Fighter, List<FieldPoint>>>,
    pos: FieldPoint,
    exploredPositions: MutableSet<FieldPoint>
): Boolean {
    var enemiesFoundOnCurrentDistance = false
    val newPositions = mutableListOf<FieldPoint>()

    listOf(Pair(0, -1), Pair(-1, 0), Pair(1, 0), Pair(0, 1))
        .forEach { direction ->
            val newPos = FieldPoint(pos.x + direction.first, pos.y + direction.second)

            if (!exploredPositions.contains(newPos)) {
                exploredPositions.add(newPos)

                val otherFighter =
                    sortedFighters.firstOrNull { f -> f.position.x == newPos.x && f.position.y == newPos.y }

                if (otherFighter != null && otherFighter.type == enemyType) {
                    // Enemy found
                    enemiesFoundOnCurrentDistance = true
                    enemies.add(Pair(otherFighter, previousPositions))
                    println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " found enemy:" + "  newPos: $newPos")
                } else if (!wallFieldPoints.contains(newPos) && otherFighter == null) {
                    // Dot position found
                    newPositions.add(newPos)
                    println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + "  newPos: $newPos")
                }
            }
        }

    if (!enemiesFoundOnCurrentDistance && newPositions.isNotEmpty()) {
        for (newPos in newPositions) {
            val newPreviousPositions = previousPositions.toMutableList()
            newPreviousPositions.add(newPos)
            if (findClosestEnemies(
                    enemyType,
                    fighter,
                    wallFieldPoints,
                    sortedFighters,
                    newPreviousPositions,
                    enemies,
                    newPos,
                    exploredPositions
                )
            ) {
                break
            }
        }
    }

    return enemiesFoundOnCurrentDistance

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
data class Fighter(var position: FieldPoint, val type: Type, var hitCount: Int = 200, var attackPower: Int = 3)
enum class Type(val code: String) {
    GOBLIN("G"), ELF("E"), WALL("#"), DOT(".")
}

fun printGame(fieldSize: Pair<Int, Int>, wallFieldPoints: MutableSet<FieldPoint>, fighters: MutableList<Fighter>) {
    for (y in 0 until fieldSize.second) {
        println()
        for (x in 0 until fieldSize.first) {
            val fighter = fighters.filter { it.hitCount > 0 }.firstOrNull { it.position.x == x && it.position.y == y }
            if (wallFieldPoints.contains(FieldPoint(x, y))) {
                print('#')
            } else if (fighter != null) {
                print(fighter.type.code)
            } else {
                print(".")
            }
        }
    }
    println()
}

fun debugPrint(fighter: Fighter, chosenEnemy: Fighter, prefix: String, message: String) {
    println(prefix + ": " + fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " - (" + fighter.hitCount + ") " + message + " " + chosenEnemy.type.toString() + ":" + chosenEnemy.position.x + "," + chosenEnemy.position.y + " - (" + fighter.hitCount + ") ")
}
