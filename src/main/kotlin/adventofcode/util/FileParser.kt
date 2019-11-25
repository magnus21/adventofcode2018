package adventofcode.util

import java.io.File

class FileParser {
    companion object Parser {
        fun getFileRows(year: Int, fileName: String): List<String> {
            return File("src/main/resources/v$year/$fileName").readLines()
        }
    }
}