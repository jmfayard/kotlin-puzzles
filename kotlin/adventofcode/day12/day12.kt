package adventofcode.day12

import java.io.File
import kotlin.text.MatchResult
import kotlin.text.Regex

fun main(args: Array<String>) {
    val file = File("kotlin/adventofcode/day12/input.txt")
    check(file.canRead())
    val s: String = file.readBytes().toString("UTF-8")
    val regexp = Regex(pattern = "-?(\\d+)" )
    val sum = regexp.findAll(s).sumBy { r: MatchResult -> Integer.valueOf(r.value) }
    println("sum = $sum")

}