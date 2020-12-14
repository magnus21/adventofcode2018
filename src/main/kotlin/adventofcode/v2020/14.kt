package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.math.pow
import kotlin.system.measureTimeMillis

object Day14 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "14.txt")
        val program = Day14.parseInput(input)

        // Run program.
        val time = measureTimeMillis {
            println("Part 1: ${part1(program)}")
            println("Part 2: ${part2(program)}")
        }
        println("Time: ($time milliseconds)")
    }

    private fun part1(programs: List<Program>): Long {
        val memory = mutableMapOf<Long, Long>()

        programs.forEach { p ->
            p.instructions.forEach { ins ->
                val value = Integer.toBinaryString(ins.second)!!
                memory[ins.first.toLong()] = maskValue(p.mask, value)
            }
        }

        return memory.values.sum();
    }

    private fun maskValue(mask: CharArray, value: String): Long {
        val paddedValue = value.padStart(36, '0')

        return toDecimal(mask.mapIndexed { i, v -> if (v != 'X') v else paddedValue[i] })
    }

    private fun toDecimal(it: List<Char>): Long {
        return it.reversed()
            .mapIndexed { pos, bit -> if (bit == '1') 2.0.pow(pos.toDouble()).toLong() else 0L }
            .sum()
    }

    private fun part2(programs: List<Program>): Long {
        val memory = mutableMapOf<Long, Long>()

        programs.forEach { p ->
            p.instructions.forEach { ins ->
                val address = Integer.toBinaryString(ins.first)!!
                maskAddress(p.mask, address).forEach { memory[it] = ins.second.toLong() }
            }
        }

        return memory.values.sum();
    }

    private fun maskAddress(mask: CharArray, address: String): List<Long> {
        val paddedValue = address.padStart(36, '0')
        val result = mask.mapIndexed { i, v -> if (v == '0') paddedValue[i] else v }

        val addresses = if (result.count { it == 'X' } == 0) listOf(result) else generateAddresses(result)

        return addresses.map { toDecimal(it) }
    }


    private fun generateAddresses(mutableAddress: List<Char>): List<List<Char>> {
        return mutableAddress.foldIndexed(listOf()) { i, list, value ->
            when (value) {
                'X' -> when {
                    list.isEmpty() -> getExpandedAddresses(mutableAddress, i)
                    else -> list.toMutableList().flatMap { a -> getExpandedAddresses(a, i) }
                }
                else -> list
            }
        }
    }

    private fun getExpandedAddresses(
        a: List<Char>,
        i: Int
    ): List<List<Char>> {
        return listOf('0', '1').map {
            a.take(i).plusElement(it)
                .plus(a.takeLast(a.size - i - 1))
        }
    }

    private fun parseInput(input: List<String>): List<Program> {
        return input.fold(mutableListOf()) { programs, line ->
            when {
                line.startsWith("mask") -> programs.add(
                    Program(
                        line.split("=")[1].trim().toCharArray(),
                        mutableListOf()
                    )
                )
                else -> {
                    val parts = line.split("[", "]", "=")
                    programs.last().instructions.add(Pair(parts[1].toInt(), parts[3].trim().toInt()))
                }
            }
            programs
        }
    }

    data class Program(val mask: CharArray, val instructions: MutableList<Pair<Int, Int>>)
}