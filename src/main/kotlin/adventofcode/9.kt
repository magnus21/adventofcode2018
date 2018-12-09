package adventofcode

fun main(args: Array<String>) {

    val input = "466 players; last marble is worth 71436 points"
    //val input = "9 players; last marble is worth 50 points" // 32

    val parts = input.split(" ")
    val nrOfPlayers = Integer.valueOf(parts[0])
    val lastMarble = Integer.valueOf(parts[6])

    val multipleValue = 23
    val players = (1..nrOfPlayers).map { 0 }.toMutableList()
    val marbles = mutableListOf(0)
    var currentMarble = 0
    var nextMarble = 1
    var currentPlayer = 1

    // head body tail
    val head = mutableListOf<Int>()
    val body = mutableListOf(0)
    val tail = mutableListOf<Int>()
    while (nextMarble <= lastMarble) {
        val currentMarblePosition = marbles.indexOf(currentMarble)

        if(nextMarble % multipleValue == 0) {
            val positionForAdditionalMarble = getPositionForMarbleStepsAwayCounterClockwise(7, currentMarble, marbles)
            val score = nextMarble + marbles.removeAt(positionForAdditionalMarble)
            players[currentPlayer - 1] += score
            currentMarble = marbles[getPositionForMarbleStepsAwayCounterClockwise(6, currentMarble, marbles)]
        } else {
            if (currentMarblePosition == marbles.size - 1) {
                marbles.add(1, nextMarble)
            } else {
                marbles.add(currentMarblePosition + 2, nextMarble)
            }
            currentMarble = nextMarble
        }

        //print("[$currentPlayer] ")
        //marbles.map { if (it == currentMarble) "($it)" else "$it" }.forEach { print("$it ") }
        //println()

        nextMarble++
        currentPlayer = getNextPlayer(currentPlayer, players.size)
    }

    println(players.mapIndexed { player, score ->  Pair(player, score) }.sortedByDescending { it.second }.first())

}

fun getPositionForMarbleStepsAwayCounterClockwise(steps: Int, currentMarble: Int, marbles: MutableList<Int>): Int {
    val position = marbles.indexOf(currentMarble) - steps
    if (position < 0) {
        return marbles.size + position
    }
    return position
}

fun getNextPlayer(currentPlayer: Int, nrOfPlayers: Int): Int {
    return if (currentPlayer < nrOfPlayers) currentPlayer + 1 else 1
}