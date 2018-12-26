package adventofcode

import adventofcode.Day24.Army.IMMUNE_SYSTEM
import adventofcode.Day24.Army.INFECTION
import java.io.File


fun main(args: Array<String>) {

    Day24.start(File("src/main/resources/24.txt").readLines())
}

object Day24 {

    enum class Army(val description: String) {
        IMMUNE_SYSTEM("Immune system"),
        INFECTION("Infection")
    }

    class Group(
        val number: Int,
        val army: Army,
        val hitPoints: Int,
        val attackType: String,
        var attackDamage: Int,
        val initiative: Int,
        val weaknesses: List<String>,
        val immunities: List<String>,
        var unitCount: Int,
        var startUnitCount: Int,
        var currentTarget: Group? = null
    ) {
        fun getEffectivePower(): Int {
            return attackDamage * unitCount
        }
    }

    fun start(input: List<String>) {
        val groups = parseGame(input)

        do {
            simulateCombat(groups)

            printCombatState(groups)

            // Edge case is that no one can win if immunities prevent any more damage.
            val winningArmy = groups
                .filter { it.unitCount > 0 }
                .groupBy { it.army }
                .map { Pair(it.key, it.value.map { it.unitCount }.sum()) }
                .sortedBy { it.second }
                .first().first

            println("winningArmy: ${winningArmy.description} with " +
                    groups
                        .filter { it.unitCount > 0 }
                        .map { it.unitCount }
                        .sum() +
                    " units"
            )

            // Reset/update game
            groups.forEach {
                it.unitCount = it.startUnitCount
                if (it.army == IMMUNE_SYSTEM) {
                    it.attackDamage++
                }
            }
        } while (winningArmy != IMMUNE_SYSTEM)
    }

    private fun simulateCombat(groups: MutableList<Group>) {

        var prevTotalUnitCount = -1
        do {
            //printCombatState(groups)

            //target selection
            val chosenTargets = mutableSetOf<Group>()
            groups
                .filter { it.unitCount > 0 }
                .sortedWith(compareByDescending<Group> { it.getEffectivePower() }.thenByDescending { it.initiative })
                .forEach { attacker ->
                    val possibleTargets = groups
                        .filter { it.army != attacker.army }
                        .filter { it.unitCount > 0 }
                        .filter { !chosenTargets.contains(it) }
                        .filter { getAttackDamagePower(attacker, it) > 0 }
                        .sortedWith(compareByDescending<Group> {
                            getAttackDamagePower(
                                attacker,
                                it
                            )
                        }.thenByDescending { it.getEffectivePower() }.thenByDescending { it.initiative })

                    possibleTargets
                        .forEach { target ->
                            //println(attacker.army.description + " group ${attacker.number} would deal defending group ${target.number} ${getAttackDamagePower(attacker, target)} damage")
                        }

                    attacker.currentTarget = possibleTargets.firstOrNull()
                    if (attacker.currentTarget != null) {
                        chosenTargets.add(attacker.currentTarget!!)
                    }
                }

            //println()

            // attacking
            groups
                .filter { it.currentTarget != null }
                .sortedByDescending { it.initiative }
                .filter { it.unitCount > 0 } // Filter after intermediate sort operation.
                .forEach { attacker ->
                    //if (attacker.unitCount > 0) {
                        val attackDamage = getAttackDamagePower(attacker, attacker.currentTarget!!)
                        val lostUnits = attackDamage / attacker.currentTarget!!.hitPoints

                        val actualLostUnits =
                            if (attacker.currentTarget!!.unitCount >= lostUnits) lostUnits else attacker.currentTarget!!.unitCount
                        attacker.currentTarget!!.unitCount -= actualLostUnits

                        // println(attacker.army.description + " group ${attacker.number} attacks defending group ${attacker.currentTarget!!.number} killing " + actualLostUnits + " units")
                   // }

                    if (getAliveArmies(groups).size == 1) {
                        return
                    }
                }

            val totalUnitCount = groups.map { it.unitCount }.sum()
            if (prevTotalUnitCount == totalUnitCount) {
                // No one can win.
                break
            }
            prevTotalUnitCount = totalUnitCount

        } while (getAliveArmies(groups).size > 1)
    }

    private fun printCombatState(groups: MutableList<Group>) {
        println("========================================================")
        println("Immune System:")
        groups
            .filter { it.army == IMMUNE_SYSTEM }
            .filter { it.unitCount > 0 }
            .forEach { println("Group ${it.number} contains " + it.unitCount + " units") }

        println("Infection:")
        groups
            .filter { it.army == INFECTION }
            .filter { it.unitCount > 0 }
            .forEach { println("Group ${it.number} contains " + it.unitCount + " units") }

        println()

    }

    private fun getAliveArmies(groups: MutableList<Group>): List<Army> {
        return groups
            .filter { it.unitCount > 0 }
            .groupBy { it.army }
            .map { it.key }
    }

    private fun getAttackDamagePower(attacker: Group, defender: Group): Int {
        val attackFactor = when {
            defender.weaknesses.contains(attacker.attackType) -> 2
            defender.immunities.contains(attacker.attackType) -> 0
            else -> 1
        }
        return attacker.getEffectivePower() * attackFactor
    }

    /**
    Example:

    Immune System:
    17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2
    989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3

    Infection:
    801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1
    4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4
     */
    private fun parseGame(input: List<String>): MutableList<Group> {
        val groups = mutableListOf<Group>()

        var army = IMMUNE_SYSTEM
        var number = 1
        for (row in input) {
            if (row.startsWith("Infection")) {
                army = INFECTION
                number = 1
            }
            if (row.isBlank() || row.startsWith("Immune") || row.startsWith("Infection")) {
                continue
            }

            val digitsMatchResults = "(\\d+)".toRegex().findAll(row).toList().map { str -> Integer.valueOf(str.value) }
            val attackType = row.reversed().split(" ")[4].reversed()

            val weaknesses = mutableListOf<String>()
            val immunities = mutableListOf<String>()

            val traitsPartsResult = "\\(.+\\)".toRegex().find(row)
            if (traitsPartsResult != null) {
                val traitsParts = traitsPartsResult.value.drop(1).dropLast(1).split("; ")

                traitsParts.forEach { part ->
                    if (part.trim().startsWith("immune to")) {
                        immunities.addAll(part.trim().drop("immune to".length).trim().split(", "))
                    } else {
                        weaknesses.addAll(part.trim().drop("weak to".length).trim().split(", "))
                    }
                }
            }

            groups.add(
                Group(
                    number,
                    army,
                    digitsMatchResults[1],
                    attackType,
                    digitsMatchResults[2],
                    digitsMatchResults[3],
                    weaknesses,
                    immunities,
                    digitsMatchResults[0],
                    digitsMatchResults[0]
                )
            )

            number++
        }

        return groups
    }
}
