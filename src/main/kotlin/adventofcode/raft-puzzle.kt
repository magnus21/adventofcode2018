package adventofcode

import java.io.File

object RaftPuzzle {

    @JvmStatic
    fun main(args: Array<String>) {
        val logs = File("src/main/resources/raft-puzzle.txt")
            .readLines()
            .mapIndexed { i, row -> Log(i + 1, row.split(",").map { it.toInt() }) }

        println(logs)


        val logRaft = LogRaft(listOf(), listOf(), logs)
        findWorkingRafts(logRaft)
        println("Found ${workingRafts.size} solutions:")
        workingRafts.forEach {
            println(it)
            println("==========")
        }
    }

    private val workingRafts = mutableListOf<LogRaft>()

    private fun findWorkingRafts(raft: LogRaft) {

        if (!raft.fits()) {
            return
        }

        if (raft.looseLogs.isEmpty() && raft.fits()) {
            workingRafts.add(raft)
        } else {
            val addToBottom = raft.looseLogs.size % 2 == 0
            for (log in raft.looseLogs) {
                val raft1 = if (addToBottom) LogRaft(raft.bottomLogs.plus(log), raft.topLogs, raft.looseLogs.minus(log))
                else LogRaft(raft.bottomLogs, raft.topLogs.plus(log), raft.looseLogs.minus(log))

                findWorkingRafts(raft1)

                val flippedLog = Log(log.nr, log.joints.reversed())
                val raft2 =
                    if (addToBottom) LogRaft(raft.bottomLogs.plus(flippedLog), raft.topLogs, raft.looseLogs.minus(log))
                    else LogRaft(raft.bottomLogs, raft.topLogs.plus(flippedLog), raft.looseLogs.minus(log))

                findWorkingRafts(raft2)
            }
        }
    }

    data class Log(val nr: Int, val joints: List<Int>)

    data class LogRaft(val bottomLogs: List<Log>, val topLogs: List<Log>, val looseLogs: List<Log>) {

        fun fits(): Boolean {
            return bottomLogs.mapIndexed { bli, bl ->
                val positionedTlJoints = topLogs.map { it.joints[bli] }
                positionedTlJoints.isEmpty() ||
                        bl.joints.mapIndexed { i, joint -> positionedTlJoints.size <= i || positionedTlJoints[i] + joint == 0 }
                            .all { it }
            }.all { it }
        }

        override fun toString(): String {
            return logsToString(bottomLogs.plus(topLogs))
        }

        private fun logsToString(logs: List<Log>): String {
            return logs.joinToString("\n") { log ->
                log.joints.joinToString("") {
                    when (it) {
                        0 -> "-"
                        1 -> "i"
                        else -> "o"
                    }
                }
            }
        }
    }
}