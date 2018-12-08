package adventofcode

import java.io.File

fun main(args: Array<String>) {

    val stepsRaw = File("src/main/resources/7.txt").readLines()

    val steps = stepsRaw.map { step -> parseStep(step) }

    val groupedSteps: MutableList<Step> = steps.groupBy { it.first }
        .map { Step(it.key, it.value.map { it.second }.toSet()) }
        .sortedByDescending { it.mustBeBeforeSteps.size }
        .toMutableList()

    // Add steps that don't need to be before any other.
    groupedSteps.addAll(
        steps.map { it.second }
            .filter { secondLetter -> !groupedSteps.map { step -> step.letter }.contains(secondLetter) }.distinct()
            .map { Step(it, setOf()) }
    )
    //groupedSteps.forEach { println(it) }

    val result = mutableListOf<Char>()
    while(result.size < groupedSteps.size) {
        val availableSteps = getAvailableSteps(groupedSteps, result)

        if(availableSteps.isNotEmpty()) {
            result.add(availableSteps[0].letter)
        }
    }

    print("Result 1: ") // OKBNLPHCSVWAIRDGUZEFMXYTJQ
    result.forEach { print(it) }

    val result2 = mutableListOf<Char>()
    val workers = Array(5) { i -> Worker('-', 0)}
    var seconds = 0
    do {
        val availableSteps = getAvailableSteps(groupedSteps, result2)

        availableSteps.filter {  !workers.map { it.letter }.contains(it.letter)  }.forEach {
            for (pos in 0 until workers.size) {
                if (workers[pos].timeLeft <= 0) {
                    workers[pos].letter = it.letter
                    workers[pos].timeLeft = it.letter.toInt() - 64 + 60 // Ascii A = 65
                    break
                }
            }
        }

        val nextWorkerToFinish = workers.filter { it.timeLeft > 0 }.sortedBy { it.timeLeft }.first()
        val timeLeft = nextWorkerToFinish.timeLeft

        for (pos in 0 until workers.size) {
            workers[pos].timeLeft -= timeLeft
            if(workers[pos].timeLeft == 0) {
                result2.add(workers[pos].letter)
            }
        }
        seconds += timeLeft

    } while(result2.size < groupedSteps.size)

    println("\nResult 2: $seconds")
}

private fun getAvailableSteps(groupedSteps: MutableList<Step>, result: MutableList<Char>): List<Step> {
    val notAddedSteps = groupedSteps.filter { !result.contains(it.letter) }
    return notAddedSteps
        .filter { notAddedSteps.none { step -> step.mustBeBeforeSteps.contains(it.letter) } }
        .sortedBy { it.letter }
}

fun parseStep(step: String): Pair<Char, Char> {
    val parts = step.split(" ")
    return Pair(parts[1].toCharArray()[0], parts[7].toCharArray()[0])
}

data class Step(val letter: Char, val mustBeBeforeSteps: Set<Char>)
data class Worker(var letter: Char, var timeLeft: Int)

