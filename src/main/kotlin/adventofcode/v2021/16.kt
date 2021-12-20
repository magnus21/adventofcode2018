package adventofcode.v2021

import adventofcode.util.FileParser
import kotlin.math.pow
import kotlin.system.measureTimeMillis

object Day16 {

    @JvmStatic
    fun main(args: Array<String>) {
        val bits = FileParser.getFileRows(2021, "16.txt")
            .first()
            .map { hexToPaddedBinary(it) }.joinToString("")

        val time1 = measureTimeMillis {
            val answer = getVersionSum(parsePacket(bits).first)
            println("answer part 1: $answer")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {
            val answer = evaluate(parsePacket(bits).first)
            println("answer part 2: $answer")
        }
        println("Time: $time2 ms")
    }

    private fun getVersionSum(rootPacket: Packet): Long {
        return rootPacket.version + rootPacket.subPacket.sumOf { getVersionSum(it) }
    }

    private fun evaluate(packet: Packet): Long {
        return when (packet.typeId) {
            0 -> packet.subPacket.sumOf { evaluate(it) }
            1 -> packet.subPacket.map { evaluate(it) }.reduce { acc, v -> acc * v }
            2 -> packet.subPacket.map { evaluate(it) }.minByOrNull { it }!!
            3 -> packet.subPacket.map { evaluate(it) }.maxByOrNull { it }!!
            4 -> packet.value
            5 -> if (evaluate(packet.subPacket[0]) > evaluate(packet.subPacket[1])) 1 else 0
            6 -> if (evaluate(packet.subPacket[0]) < evaluate(packet.subPacket[1])) 1 else 0
            else -> if (evaluate(packet.subPacket[0]) == evaluate(packet.subPacket[1])) 1 else 0
        }
    }

    private fun parsePacket(bits: String): Pair<Packet, String> {
        val (version, typeId) = getHeader(bits)

        if (typeId == 4) {
            val (literal, remaining) = parseLiteralValue(bits.substring(6))
            return Pair(Packet(version, typeId, literal), remaining)
        }

        return if (bits.substring(6, 7) == "0") {
            val subPacketsBitLength = binToDec(bits.substring(7, 22)).toInt()
            val stopPredicate = { startBits: String, bitsToLeftToParse: String, _: Int ->
                startBits.length - bitsToLeftToParse.length < subPacketsBitLength
            }

            val (subPackets, remaining) = parseSubPackets(bits.substring(22), stopPredicate)
            Pair(Packet(version, typeId, subPacket = subPackets), remaining)
        } else {
            val nrOfSubPacket = binToDec(bits.substring(7, 18)).toInt()
            val stopPredicate = { _: String, _: String, parsedPacket: Int -> parsedPacket < nrOfSubPacket }

            val (subPackets, remaining) = parseSubPackets(bits.substring(18), stopPredicate)
            Pair(Packet(version, typeId, subPacket = subPackets), remaining)
        }
    }

    private fun parseSubPackets(
        bits: String,
        stopPredicate: (String, String, Int) -> Boolean
    ): Pair<List<Packet>, String> {
        val packets = mutableListOf<Packet>()
        var bitsToLeftToParse = bits
        var i = 0
        while (stopPredicate.invoke(bits, bitsToLeftToParse, i)) {
            val (packet, remaining) = parsePacket(bitsToLeftToParse)
            packets.add(packet)
            bitsToLeftToParse = remaining
            i++
        }
        return Pair(packets, bitsToLeftToParse)
    }

    private fun parseLiteralValue(input: String): Pair<Long, String> {
        var binaryString = ""
        var i = 0
        while (true) {
            binaryString += input.substring(i + 1, i + 5)
            val groupStartBit = input[i]
            i += 5
            if (groupStartBit == '0') {
                break
            }
        }

        val remaining = if (i == input.length) "" else input.substring(i)
        return Pair(binToDec(binaryString), remaining)
    }

    private fun getHeader(bits: String): Pair<Int, Int> =
        Pair(binToDec(bits.substring(0, 3)).toInt(), binToDec(bits.substring(3, 6)).toInt())

    private fun binToDec(binaryString: String): Long {
        return binaryString.reversed()
            .foldIndexed(0) { i, sum, c -> sum + c.digitToInt() * 2.0.pow(i.toDouble()).toLong() }
    }

    private fun hexToPaddedBinary(it: Char) =
        Integer.toBinaryString(Integer.parseInt(it.toString(), 16)).padStart(4, '0')

    data class Packet(val version: Int, val typeId: Int, val value: Long = 0, val subPacket: List<Packet> = listOf())
}