package adventofcode.v2015

import adventofcode.util.FileParser
import sun.security.provider.MD5
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

object Day4 {

    @JvmStatic
    fun main(args: Array<String>) {

        val secretKey = "bgvyzdsv"
        val time1 = measureTimeMillis {

            var i = 1
            while(true) {
                if(md5(secretKey + i.toString()).startsWith("00000")) {
                    break
                }
                i++
            }

            println("Part 1: $i")
        }
        println("Time: $time1 ms")

        val time2 = measureTimeMillis {

            var i = 1
            while(true) {
                if(md5(secretKey + i.toString()).startsWith("000000")) {
                    break
                }
                i++
            }

            println("Part 2: $i")
        }
        println("Time: $time2 ms")
    }

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}