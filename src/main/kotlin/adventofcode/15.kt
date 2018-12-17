package adventofcode

import adventofcode.Type.ELF
import adventofcode.Type.GOBLIN
import java.io.File


fun main(args: Array<String>) {

    val input = File("src/main/resources/15.txt").readLines()

    val (fieldSize, wallFieldPoints, fighters) = parseGame(input)

    println(fighters)
    printGame(fieldSize, wallFieldPoints, fighters)

    // TODO: Add path selection debug print.

    var combatOver = false
    var roundCount = 0
    do {
        val chosenPaths = mutableListOf<FieldPoint>()

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
                    // TODO: chosenPath
                    doTurnFor(fighter, fighters.filter { it.hitPoints > 0 }.toMutableList(), wallFieldPoints,chosenPaths)
                }
            }
            roundCount++
        }

        // Sort fighter after each round.
        fighters.sortWith(compareBy({ it.position.y }, { it.position.x }))

        println("\nAfter $roundCount rounds:")
        printGame(fieldSize, wallFieldPoints, fighters, chosenPaths)

    } while (!combatOver)

    val hitPointsLeft = fighters.filter { it.hitPoints > 0 }.map { it.hitPoints }.sum()

    println("$roundCount * $hitPointsLeft: " + roundCount * hitPointsLeft)
    //between 188300 - 190999 .. 71 * 2683: 190493
}

fun doTurnFor(
    fighter: Fighter,
    sortedFighters: MutableList<Fighter>,
    wallFieldPoints: MutableSet<FieldPoint>,
    chosenPath: MutableList<FieldPoint>
) {
    val enemyType = if (fighter.type == GOBLIN) ELF else GOBLIN
    val enemyPaths = mutableSetOf<Pair<Fighter, List<FieldPoint>>>()
    findClosestEnemies(
        enemyType,
        fighter,
        wallFieldPoints,
        sortedFighters,
        enemyPaths,
        mutableListOf(mutableListOf(fighter.position)),
        mutableSetOf(fighter.position)
    )

    val neighbourEnemies = enemyPaths
        .filter { it.second.size == 1 }
        .map { it.first }

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
            .sortedWith(
                compareBy(
                    { it.second.size },
                    { it.second.last().y },
                    { it.second.last().x },
                    { it.second.drop(1).first().y },
                    { it.second.drop(1).first().x }

                )
            )
            .first()

        val chosenEnemy = chosenEnemyPair.first

        chosenPath.addAll(chosenEnemyPair.second)

        println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " moves toward " + chosenEnemy.type.toString() + ":" + chosenEnemy.position.x + "," + chosenEnemy.position.y)

        // Move
        fighter.position.x = chosenEnemyPair.second[1].x
        fighter.position.y = chosenEnemyPair.second[1].y

        if (chosenEnemyPair.second.size == 2) {
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
    enemyPaths: MutableSet<Pair<Fighter, List<FieldPoint>>>,
    positions: MutableList<MutableList<FieldPoint>>,
    reachedPositions: MutableSet<FieldPoint>
) {
    val nextPositions = getNeighbours(
        positions,
        reachedPositions,
        sortedFighters,
        enemyType,
        enemyPaths,
        wallFieldPoints
    )

    if (enemyPaths.isNotEmpty()) {
        return
    }

    if (nextPositions.isNotEmpty()) {
        findClosestEnemies(
            enemyType,
            fighter,
            wallFieldPoints,
            sortedFighters,
            enemyPaths,
            nextPositions,
            reachedPositions
        )
    }
}

private fun getNeighbours(
    positions: MutableList<MutableList<FieldPoint>>,
    reachedPositions: MutableSet<FieldPoint>,
    sortedFighters: MutableList<Fighter>,
    enemyType: Type,
    enemyPaths: MutableSet<Pair<Fighter, List<FieldPoint>>>,
    wallFieldPoints: MutableSet<FieldPoint>
): MutableList<MutableList<FieldPoint>> {
    val nextPositions = mutableListOf<MutableList<FieldPoint>>()

    val sortDrop = if (positions[0].size > 1) 1 else 0
    val reachedPositionsInDepth = mutableSetOf<FieldPoint>()

    positions
       //.sortedWith(compareBy({ it.drop(sortDrop).first().y }, { it.drop(sortDrop).first().x }))
        .forEach { positionList ->
            listOf(Pair(0, -1), Pair(-1, 0), Pair(1, 0), Pair(0, 1))
                .map { direction ->
                    FieldPoint(
                        positionList.last().x + direction.first,
                        positionList.last().y + direction.second
                    )
                }
                .forEach { newPos ->
                    if (!reachedPositions.contains(newPos)) {
                        val otherFighter =
                            sortedFighters.firstOrNull { f -> f.position.x == newPos.x && f.position.y == newPos.y }

                        if (otherFighter != null && otherFighter.type == enemyType) {
                            // Enemy found
                            reachedPositions.add(newPos)
                            val previousPositionsList = mutableListOf<FieldPoint>()
                            previousPositionsList.addAll(positionList)

                            enemyPaths.add(Pair(otherFighter, previousPositionsList))
                            //println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + " found enemy:" + "  newPos: $newPos")
                        } else if (!wallFieldPoints.contains(newPos) && otherFighter == null) {
                            // Dot position found
                            val previousPositionsList = mutableListOf<FieldPoint>()
                            previousPositionsList.addAll(positionList)
                            previousPositionsList.add(newPos)

                            nextPositions.add(previousPositionsList)
                            reachedPositions.add(newPos)
                            //println(fighter.type.toString() + ":" + fighter.position.x + "," + fighter.position.y + "  newPos: $newPos, previousPositions: " + previousPositions)
                        }
                    }
                }
        }

    //reachedPositions.addAll(reachedPositionsInDepth)

    return nextPositions
        //.sortedWith(compareBy({ it.drop(1).first().y }, { it.drop(1).first().x },{ it.last().y }, { it.last().x }))
       // .groupBy { it.last() }
       // .map { it.value.first() }
       // .toMutableList()
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

fun printGame(
    fieldSize: Pair<Int, Int>,
    wallFieldPoints: MutableSet<FieldPoint>,
    fighters: MutableList<Fighter>,
    chosenPaths: MutableList<FieldPoint> = mutableListOf()
) {
    print("    ")
    for (x in 0 until fieldSize.first) {
        print(x%10)
    }
    println()
    for (y in 0 until fieldSize.second) {
        print(y.toString().padStart(2,'0') + ": ")
        for (x in 0 until fieldSize.first) {
            val fighter = fighters.filter { it.hitPoints > 0 }.firstOrNull { it.position.x == x && it.position.y == y }
            if (wallFieldPoints.contains(FieldPoint(x, y))) {
                print('#')
            } else if (fighter != null) {
                print(fighter.type.code)
            } else if(chosenPaths.contains(FieldPoint(x, y))) {
                print(chosenPaths.filter {  it == FieldPoint(x, y) }.size % 10)
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
