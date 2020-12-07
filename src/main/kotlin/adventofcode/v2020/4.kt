package adventofcode.v2020

import adventofcode.util.FileParser
import adventofcode.v2020.Day4.PassportField.*

object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "4.txt")
        val passports = parsePassports(input)

        part1(passports)
        part2(passports)
    }

    enum class PassportField { byr, iyr, eyr, hgt, hcl, ecl, pid, cid }

    private fun part1(passports: List<Map<PassportField, String>>) {
        println("Part 1: ${passports.filter { isValidPart1(it) }.size}")
    }

    private fun part2(passports: List<Map<PassportField, String>>) {
        println("Part 2: ${passports.filter { isValidPart2(it) }.size}")
    }

    private fun isValidPart1(passport: Map<PassportField, String>): Boolean {
        return passport.size == 8 || (passport.size == 7 && !passport.containsKey(cid))
    }

    /*
    byr (Birth Year) - four digits; at least 1920 and at most 2002.
    iyr (Issue Year) - four digits; at least 2010 and at most 2020.
    eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
    hgt (Height) - a number followed by either cm or in:
    If cm, the number must be at least 150 and at most 193.
    If in, the number must be at least 59 and at most 76.
    hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
    ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
    pid (Passport ID) - a nine-digit number, including leading zeroes.
    cid (Country ID) - ignored, missing or not.
     */
    private fun isValidPart2(passport: Map<PassportField, String>): Boolean {
        return isValidPart1(passport) &&
                isValidNumber(passport[byr]!!, 1920, 2002, 4) &&
                isValidNumber(passport[iyr]!!, 2010, 2020, 4) &&
                isValidNumber(passport[eyr]!!, 2020, 2030, 4) &&
                isValidHeight(passport[hgt]!!) &&
                isValidHairColor(passport[hcl]!!) &&
                isValidEyeColor(passport[ecl]!!) &&
                isValidPid(passport[pid]!!)
    }

    private fun isValidHeight(height: String): Boolean {
        val isCm = height.endsWith("cm")
        val isIn = height.endsWith("in")
        return (isCm && isValidNumber(height.dropLast(2), 150, 193, 3)) ||
                (isIn && isValidNumber(height.dropLast(2), 59, 76, 2))
    }

    private fun isValidHairColor(hairColor: String): Boolean {
        return hairColor.length == 7 && hairColor[0] == '#' && hairColor.drop(1).matches("[0-9a-f]{6}".toRegex())
    }

    private fun isValidNumber(number: String, min: Int, max: Int, count: Int): Boolean {
        return number.filter { it.isDigit() }.count() == count && number.toInt() >= min && number.toInt() <= max
    }

    private fun isValidEyeColor(eyeColor: String): Boolean {
        return setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(eyeColor)
    }


    private fun isValidPid(pid: String): Boolean {
        return pid.filter { it.isDigit() }.count() == 9
    }


    private fun parsePassports(input: List<String>): List<Map<PassportField, String>> {
        val passports = mutableListOf<Map<PassportField, String>>()

        var passport = mutableMapOf<PassportField, String>()
        for (row in input) {
            if (row == "") {
                passports.add(passport.toMutableMap())
                passport = mutableMapOf()
            } else {
                passport.putAll(
                    row.split(" ")
                        .map {
                            val parts = it.split(":");
                            Pair(valueOf(parts[0]), parts[1])
                        }
                )
            }
        }
        return passports
    }
}


