package adventofcode.day9

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val testing = false
    val file =
            if (testing) File("kotlin/adventofcode/day9/example.txt")
            else File("kotlin/adventofcode/day9/input.txt")
    var lines = file.readLines()
    val cities = ArrayList<String>()
    lines.forEach {
        val words = it.splitToSequence(delimiters = " ")
        map.addPath(words.first(), words.elementAt(3), words.last().toInt())
    }

}
object map  {
    val cities = ArrayList<String>()

    fun addCity(name: String): Int {
        val i = cities.indexOf(name)
        if (i == -1) {
            cities.add(name)
            return cities.size-1
        } else {
            return i
        }

    }

    fun addPath(city1: String, city2: String, length: Int) {
        val i = addCity(city1)
        val j = addCity(city2)
    }

}
data class City(val name : String)

