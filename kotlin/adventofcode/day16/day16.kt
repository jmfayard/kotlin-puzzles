package adventofcode.day16

import adventofcode.day14.filename
import adventofcode.day9.map
import kotlin.properties.getValue

import org.testng.annotations.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

val sueGivingPresent = parseSue("Sue 0: children: 3, cats: 7, samoyeds: 2, pomeranians: 3, akitas: 0, vizslas: 0, goldfish: 5, trees: 3, cars: 2, perfumes: 1")


fun main(args: Array<String>) {
    val filename = "kotlin/adventofcode/day16/input.txt"
    val sues = ArrayList<Sue>()
    val keys = listOf("children", "cats", "samoyeds", "pomeranians", "akitas", "vizslas", "goldfish", "trees", "cars", "perfumes")

    println("Search for $sueGivingPresent")
    File(filename).forEachLine { sues.add(parseSue(it)) }
    val result = sues.asSequence()
            .restrict("children" to 3)
            .restrict("cats" to 7)
            .restrict("samoyeds" to 2)
            .restrict("pomeranians" to 3)
            .restrict("akitas" to 0)
            .restrict("vizslas" to 0)
            .restrict("goldfish" to 5)
            .restrict("trees" to 3)
            .restrict("cars" to 2)
            .restrict("perfumes" to 1)
            .forEach { println(it) }
}

fun Sequence<Sue>.restrict(pair: Pair<String, Int>): Sequence<Sue>
        = this.filter { s : Sue ->
    val key = pair.first
    !s.containsKey(key) || s[key] == pair.second
}





class Sue(val id: Int?, val map : Map<String, Int>) : Map<String, Int> by map {

    override fun toString(): String {
        return "Sue#${id} ${map.entries}"
    }
}


fun parseSue(line: String): Sue {
    val pos = line.indexOf(':')
    val space = line.indexOf(' ')
    val id = line.substring(space+1..pos-1).toInt()
    val map = line.substring(pos+2)
            .splitToSequence(',')
            .map { s ->
                val i = s.indexOf(':')
                Pair(s.substring(0..i-1).trim(), s.substring(i+2).toInt())
            }
            .toMap({ p -> p.first}, {p -> p.second})

    return Sue(id = id, map = map)
}


@Test
class Tests16 {

    fun create() {
        val expected = mapOf(
                "children" to 3, "cats" to 7, "samoyeds" to 2, "pomeranians" to 3,
                "akitas" to 0, "vislas" to 0, "goldfish" to 5, "trees" to 3, "cars" to 2, "perfumes" to 1
        )
        println(sueGivingPresent)
    }


}