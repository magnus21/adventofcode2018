package adventofcode.v2018

import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

fun main(args: Array<String>) {

    val log = File("src/main/resources/4.txt").readLines()

    val logRecords = log.map { logString -> parseLogRecord(logString) }.sortedBy { logRecord -> logRecord.datetime }

    val logRecordsMap = mutableMapOf<String, HashMap<String, GuardLogRecord>>()

    var currentId = ""
    var sleepMinute = 0;
    for (logRecord in logRecords) {
        extractId(logRecord).ifPresent { id -> currentId = id }
        val minute = extractMinute(logRecord)
        val date = logRecord.datetime.substring(5, 10)

        if (logRecord.text.equals("falls asleep")) {
            sleepMinute = minute
        } else if (logRecord.text.equals("wakes up")) {
            (sleepMinute until minute).forEach {
                logRecordsMap
                    .getOrPut(currentId) { HashMap() }
                    .getOrPut(date) { GuardLogRecord(date, HashSet()) }
                    .sleepMinutes.add(it)
            }
        }
    }

    // Most sleep minutes.
    val mostSleep = logRecordsMap
        .map { logRecord ->
            Pair(logRecord.key, logRecord.value.values.fold(0) { acc, glr -> acc + glr.sleepMinutes.count() })
        }.sortedByDescending { it.second }[0]

    println(mostSleep.toString() + ": " + logRecordsMap[mostSleep.first]!!.values.flatMap { it.sleepMinutes }.groupBy { it }.map { e -> Pair(e.key, e.value.size) }
        .sortedByDescending { it.second }[0])


    // Guard with most slept on minute
    println(
        logRecordsMap
            .map { logRecord ->
                val max = logRecord.value.values
                    .flatMap { it.sleepMinutes }
                    .groupBy { it }
                    .map { Pair(it.key, it.value.size) }
                    .toList().sortedByDescending { it.second }[0]

                Pair(logRecord.key, max)
            }.sortedByDescending { it.second.second }[0]
    )
}

fun extractMinute(logRecord: LogRecord): Int {
    return Integer.valueOf(logRecord.datetime.split(":")[1])
}

fun extractId(logRecord: LogRecord): Optional<String> {
    return if (logRecord.text.startsWith("Guard #")) Optional.of(logRecord.text.split("Guard #", " ")[1]) else Optional.empty()
}

fun parseLogRecord(logString: String): LogRecord {
    val recordParts = logString.split("[", "] ")
    return LogRecord(recordParts[1], recordParts[2])
}

data class LogRecord(val datetime: String, val text: String)

data class GuardLogRecord(val date: String, val sleepMinutes: MutableSet<Int>)
