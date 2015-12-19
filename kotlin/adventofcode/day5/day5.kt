package adventofcode.day5

import java.io.File

fun main(args: Array<String>) {

    val file = File("/Users/jmfayard/Dev/perso/AdventOfCode/kotlin/adventofcode/day5/strings.txt")
    var nice = 0
    var naugty = 0
    file.forEachLine { line ->
        if (line.isNice()) {
            nice++
            println("${line} is Nice")
        } else {
            naugty++
            //            println("${line} is Naughty")
        }
    }
    println("Found ${nice} nice lines and ${naugty} naugty lines")


    val tests = listOf<String>(
            "ugknbfddgicrmopn",
            "aaa",
            "jchzalrnumimnmhp",
            "haegwjzuvuyypxyu",
            "dvszwmarrgswjxmb"
    ).map { Triple(hasThreeVoyels(it), hasDouble(it), doesNotContainForbiddenWord(it)) }
    println(tests.joinToString(separator = "\n"))


}

fun String.isNice(): Boolean
        = hasThreeVoyels(this) && hasDouble(this) && doesNotContainForbiddenWord(this)

fun hasDouble(s: String): Boolean
        = s.toPairList().any { it.prev == it.next }

val forbidden = listOf<MyPair>(
        MyPair('a', 'b'),
        MyPair('c', 'd'),
        MyPair('p', 'q'),
        MyPair('x', 'y')
)

fun doesNotContainForbiddenWord(s: String): Boolean
        = s.toPairList().all { !forbidden.contains(it) }

fun String.toPairList(): List<MyPair> {
    val list = this.toCharList()
    return list.mapIndexed { i, char ->
        val prev = if (i == 0) null else list.get(i - 1)
        MyPair(prev = prev, next = char)
    }
}

data class MyPair(val prev: Char?, val next: Char?)

fun hasThreeVoyels(s: String): Boolean {
    val voyels = listOf<Char>('a', 'e', 'i', 'o', 'u')
    return s.toCharArray().filter { it in voyels }.count() >= 3
}
