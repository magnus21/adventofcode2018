package adventofcode.v2018


fun main(args: Array<String>) {

    val input = "466 players; last marble is worth 7143600 points"
    //val input = "466 players; last marble is worth 71436 points"
    //val input = "9 players; last marble is worth 25 points" // 32 22563

    val parts = input.split(" ")
    val nrOfPlayers = Integer.valueOf(parts[0])
    val lastMarbleNumber = Integer.valueOf(parts[6])

    val multipleValue = 23L
    val players = (1..nrOfPlayers).map { 0L }.toMutableList()

    val marbleRing = MarbleRing(Marble(0))
    var nextMarbleNumber = 1L
    var currentPlayer = 1

    while (nextMarbleNumber <= lastMarbleNumber) {
        val newMarble = Marble(nextMarbleNumber)

        if (nextMarbleNumber % multipleValue == 0L) {
            val additionalMarble = marbleRing.previousMarbleStepsAway(7)

            players[currentPlayer - 1] += nextMarbleNumber + additionalMarble.value

            additionalMarble.previous.next = additionalMarble.next
            additionalMarble.next.previous = additionalMarble.previous

            marbleRing.setCurrentMarble(additionalMarble.next)
        } else {
            val firstNextMarble = marbleRing.nextMarbleStepsAway(1)
            val secondNextMarble = marbleRing.nextMarbleStepsAway(2)

            firstNextMarble.next = newMarble
            secondNextMarble.previous = newMarble

            newMarble.next = secondNextMarble
            newMarble.previous = firstNextMarble

            marbleRing.setCurrentMarble(newMarble)
        }

        //print("[$currentPlayer] ")
        //marbleRing.toList().map { if (it == marbleRing.getCurrentMarble()) "($it)" else "$it" }.forEach { print("$it ") }
        //println()

        nextMarbleNumber++
        currentPlayer = if (currentPlayer < nrOfPlayers) currentPlayer + 1 else 1
    }

    println(players.mapIndexed { player, score -> Pair(player, score) }.sortedByDescending { it.second }.first())

}

class Marble(val value: Long) {
    var next: Marble = this
    var previous: Marble = this
    override fun toString(): String {
        return "$value"
    }
}

class MarbleRing(private val firstMarble: Marble) {

    private var currentMarble: Marble = firstMarble

    fun setCurrentMarble(marble: Marble) {
        currentMarble = marble
    }

    fun nextMarbleStepsAway(steps: Int): Marble {
        return marbleStepsAway(steps, true)
    }

    fun previousMarbleStepsAway(steps: Int): Marble {
        return marbleStepsAway(steps, false)
    }

    fun marbleStepsAway(steps: Int, next: Boolean): Marble {
        var marble = currentMarble
        for (step in 1..steps) {
            marble = if (next) marble.next else marble.previous
        }
        return marble
    }

    fun toList(): List<Marble> {
        val list = mutableListOf<Marble>()

        var marble = firstMarble
        do {
            list.add(marble)
            marble = marble.next
        } while (marble != firstMarble)

        return list
    }
}