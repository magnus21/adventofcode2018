package adventofcode.v2020

import adventofcode.util.FileParser
import kotlin.system.measureTimeMillis

object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {

        val input = FileParser.getFileRows(2020, "18.txt")

        val time = measureTimeMillis {
            println("Part 1: ${part1(input)}")
            println("Part 2: ${part2(input)}")
        }
        println("Time: ($time milliseconds)")
    }


    private fun part1(input: List<String>): Long {
        return input.map { line ->
            parseExp(line.toCharArray().filter { it != ' ' }.map { it.toString() }).acc
        }.sum()
    }

    private fun part2(input: List<String>): Long {
        return input
            .map { line -> line.toCharArray().filter { it != ' ' }.map { it.toString() } }
            .map { addPlusParenthesis(it) }
            .map { Pair(it, parseExp(it).acc) }
            //.forEach { println(it.first.joinToString("").replace("+", " + ").replace("*", " * ") + " = ${it.second}") }
            //.forEach { println(it) }
            .map { it.second }
            .sum()

    }

    private fun addPlusParenthesis(expression: List<String>): List<String> {
        val exp = expression.map { it[0] }.toMutableList()
        var found = true
        var startIndex = 0
        while (found) {
            found = false
            for (i in startIndex..exp.size - 3) {
                if (exp[i + 1] == '+') {
                    if (exp[i].isDigit() && exp[i + 2].isDigit()) {
                        exp.add(i, '(')
                        exp.add(i + 4, ')')
                        found = true
                        startIndex = i + 4
                        break
                    } else if (exp[i].isDigit() && exp[i + 2] == '(') {
                        val subExp = getSubExpression(exp.map { it.toString() }, i + 2)
                        exp.add(i, '(')
                        exp.add(i + 4 + subExp.size, ')')
                        found = true
                        startIndex = i + 4
                        break
                    } else if (exp[i + 2].isDigit() && exp[i] == ')') {
                        val subExp = getSubExpression(exp.map { it.toString() }, i, true)
                        exp.add(i - subExp.size, '(')
                        exp.add(i + 4, ')')
                        found = true
                        startIndex = i + 4
                        break
                    } else if (exp[i] == ')' && exp[i + 2] == '(') {
                        val subExp1 = getSubExpression(exp.map { it.toString() }, i, true)
                        val subExp2 = getSubExpression(exp.map { it.toString() }, i + 2)

                        exp.add(i + 4 + subExp2.size, ')')
                        exp.add(i - subExp1.size, '(')

                        found = true
                        startIndex = i + 4
                        break
                    }
                }
            }
        }
        return exp.map { it.toString() }
    }


    // TODO: Do this the right way...
    private fun parseExp2(expression: List<String>): Calc {


        while (true) {
            val plusOpIndex = expression.indexOf("+")
            //evaluate(expression, plusOpIndex)
        }

        var i = 0
        var calc = Calc()
        while (i < expression.size) {
            val c = expression[i]
            calc = when {
                c == "(" -> {
                    val subExp = getSubExpression(expression, i)
                    i += subExp.size
                    Calc(doMath(calc, parseExp(subExp).acc))
                }
                c == ")" -> calc
                c.toLongOrNull() != null -> Calc(doMath(calc, c.toLong()))
                else -> Calc(calc.acc, c)
            }
            i++
        }
        return calc
    }

    /* private fun evaluate(exp: MutableList<String>, opIndex: Int) {

         if (exp[opIndex - 1].toLongOrNull() != null && exp[opIndex + 1].toLongOrNull() != null) {

             val res = doMath(exp[opIndex],exp[opIndex - 1].toLong(),exp[opIndex + 1].toLong())
             exp.removeAt(opIndex + 1)
             exp.removeAt(opIndex)
             exp.removeAt(opIndex - 1)
             exp.add(opIndex - 1, res.toString())
         } else if (exp[opIndex].isDigit() && exp[i + 2] == '(') {
             val subExp = getSubExpression(exp, i + 2)
             exp.add(i, '(')
             exp.add(i + 4 + subExp.size, ')')
             found = true
             startIndex = i + 4
             break
         } else if (exp[i + 2].isDigit() && exp[i] == ')') {
             val subExp = getSubExpression(exp, i, true)
             exp.add(i - subExp.size, '(')
             exp.add(i + 4, ')')
             found = true
             startIndex = i + 4
             break
         }
     }*/

    private fun parseExp(expression: List<String>): Calc {

        var i = 0
        var calc = Calc()
        while (i < expression.size) {
            val c = expression[i]
            calc = when {
                c == "(" -> {
                    val subExp = getSubExpression(expression, i)
                    i += subExp.size
                    Calc(doMath(calc, parseExp(subExp).acc))
                }
                c == ")" -> calc
                c.toLongOrNull() != null -> Calc(doMath(calc, c.toLong()))
                else -> Calc(calc.acc, c)
            }
            i++
        }
        return calc
    }


    private fun getSubExpression(expression: List<String>, i: Int, reversed: Boolean = false): MutableList<String> {
        val result = mutableListOf<String>()
        val step = if (reversed) -1 else 1
        var index = i + step
        var level = 1
        while (true) {
            val c = expression[index]
            if (c == "(") {
                level += step
            } else if (c == ")") {
                level -= step
            }

            if (level == 0) {
                return result
            }
            result.add(c)
            index += step
        }
    }


    private fun doMath(calc: Calc, c: Long): Long {
        return if (calc.op == "*") calc.acc * c else calc.acc + c
    }

    private fun doMath(op: String, a: Long, b: Long): Long {
        return if (op == Operator.MULTIPLY.op) a * b else a + b
    }

    enum class Operator(val op: String) {
        PLUS("+"),
        MULTIPLY("*")
    }

    data class Calc(val acc: Long = 0, val op: String = "+")

}