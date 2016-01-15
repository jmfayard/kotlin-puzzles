package adventofcode.day8


import java.io.File



fun main(args: Array<String>) {
    val testing = false
    val input =
            if (testing) File("kotlin/adventofcode/day8/example.txt").readLines()
            else File("kotlin/adventofcode/day8/input.txt").readLines()
    val results = input.map { Lexer(it).analyze().wasted() }.filterNotNull()
    println(results)
    println("Total wasted: " + results.sum())
}

class Lexer(val line : String) {
    var real : Int = 0
    var error : String? = null
    var done = false

    fun wasted() : Int? = when {
        error != null || done == false -> null
        else -> line.length - real
    }

    fun analyze(): Lexer {
        real = 0
        if (line.length < 2 || line.first() != '"' || line.last() != '"') {
            error = "String must be inside simple quote"
        }
        var current = 1
        while (error == null && done == false) {
            if  (current == line.length -1) {
                done = true
            } else {
                val c = line.get(current)
                val isEscape = c == '\\'
                val isOctal = line.get(current + 1) == 'x'
                val skip = when {
                    isEscape && isOctal  -> 4
                    isEscape && !isOctal -> 2
                    else -> 1
                }
                real++
                current = current+skip
            }
        }
        return this
    }

    override fun toString(): String = "$real chars in storage=${line.length} for line = $line"
}

/**
 *
""
"abc"
"aaa\"aaa"
"\x27"

 */
