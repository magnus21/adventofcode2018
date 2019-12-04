package adventofcode.v2019

object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {
        val range = getRange("246515-739105")

        val result1 = getNrOfPossiblePasswords(range)
        println("Number of possible passwords part 1: ${result1.size}")
        // 1048

        val result2 = getNrOfPossiblePasswords(range, true)
        println("Number of possible passwords part 2: ${result2.size}")
        // 677
    }

    private fun getNrOfPossiblePasswords(range: Pair<Int, Int>, exactPair: Boolean = false): List<Int> {
        val list = mutableListOf<Int>()
        for (password in range.first..range.second) {
            if (neverDecrease(password) && hasSameNumberPair(password, exactPair)) {
                list.add(password)
            }
        }
        return list;
    }


    private fun hasSameNumberPair(password: Int, exactPair: Boolean = false): Boolean {
        val passwordStr = password.toString()
        for (i in 1..5) {
            if (passwordStr[i] == passwordStr[i - 1]
                && (!exactPair || ((i == 1 || passwordStr[i] != passwordStr[i - 2]) && (i == 5 || passwordStr[i] != passwordStr[i + 1])))
            ) {
                return true
            }
        }
        return false
    }

    private fun neverDecrease(password: Int): Boolean {
        val passwordStr = password.toString()
        for (i in 1..5) {
            if (passwordStr[i].toInt() < passwordStr[i - 1].toInt()) {
                return false
            }
        }
        return true
    }

    private fun getRange(input: String): Pair<Int, Int> {
        val list = input.split("-").map { Integer.valueOf(it) }
        return Pair(list[0], list[1])
    }
}