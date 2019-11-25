package adventofcode.v2018

import adventofcode.v2018.Type.ELF
import java.io.File


fun main(args: Array<String>) {

    val input = File("src/main/resources/15.txt").readLines()

    val (fieldSize, wallFieldPoints, startFighters) = parseGame(input)

    println(startFighters)
    printGame(fieldSize, wallFieldPoints, startFighters)

    val startElfCount = startFighters.filter { it.type == ELF }.size
    var elfAttackPower = 4
    do {
        println("elfAttackPower: $elfAttackPower")

        val fighters = startFighters.map {
            Fighter(it.position.copy(), it.type, 200, if (it.type == ELF) elfAttackPower else 3)
        }.toMutableList()

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
                        doTurnFor(
                            fighter,
                            fighters.filter { it.hitPoints > 0 }.toMutableList(),
                            wallFieldPoints,
                            chosenPaths
                        )
                    }
                }
                roundCount++
            }

            // Sort fighter after each round.
            fighters.sortWith(compareBy({ it.position.y }, { it.position.x }))

            //printGame(fieldSize, wallFieldPoints, fighters, mutableListOf())
            //println("\nAfter $roundCount rounds:")
        } while (!combatOver)

        val survivingElfsCount = fighters.filter { it.hitPoints > 0 }.filter { it.type == ELF }.size

        val hitPointsLeft = fighters.filter { it.hitPoints > 0 }.map { it.hitPoints }.sum()

        println("$roundCount * $hitPointsLeft: " + roundCount * hitPointsLeft)
        printGame(fieldSize, wallFieldPoints, fighters, mutableListOf())

        elfAttackPower++
    } while (survivingElfsCount != startElfCount)
}