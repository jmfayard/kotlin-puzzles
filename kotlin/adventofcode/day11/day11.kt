package adventofcode.day11

import org.testng.annotations.Test
import kotlin.test.assertEquals

/**
 * Created by jmfayard on 28/01/16.
 */

fun main(args: Array<String>) {
    val input = "cqjxjnds"
    input.iterate()
            .filter { secondCondition(it) }
            .filter { firstCondition(it) }
            .filter { thirdCondition(it) }
            .take(500).forEach { println(it.joinToString(separator = "")) }

}

val magic = ('a'..'x').map { c -> "$c${c+1}${c+2}"}
val confusingLetters = listOf('i', 'o', 'l')

fun firstCondition(pass: List<Char>): Boolean {
    return pass.byTriples().any { magic.contains("${it.first}${it.second}${it.third}") }
}
fun secondCondition(pass: List<Char>): Boolean = pass.all { it !in confusingLetters }
fun thirdCondition(pass : List<Char>) : Boolean =
    pass.mapIndexed { i, c ->
        if (i == pass.lastIndex) null
        else Pair(pass[i], pass[i+1])
    }.filter {
        it != null && it.first == it.second
    }.distinct()
    .count() >= 2


fun  List<Char>.byTriples() : Sequence<Triple<Char, Char, Char>> {
    check(this.size >= 3)
    val iterator = this.iterator()
    var head = Triple(iterator.next(), iterator.next(), iterator.next())
    fun nextTriple() : Triple<Char, Char, Char>? =
        if (iterator.hasNext()) {
            head = Triple(head.second, head.third, iterator.next())
            head
        } else {
            null
        }


    return sequence { nextTriple() }
}

fun tests() {
    't'.lowerCases().forEach { print(it) }
    '0'.lowerCases().forEach { print(it) }
    "aaaaaayt".iterate().take(50).forEach { println(it.joinToString(separator = "")) }
    "zzzzzzza".iterate().take(50).forEach { println(it.joinToString(separator = "")) }
}


fun String.iterate() : Sequence<List<Char>> {
    check(this.length == 8, { println("password must have 8 characters")})
    check(this.all { it in 'a'..'z' }, { println("Only [a-z] are allowed")})
    var head = this.toArrayList();
    var index = 7

    var charIterator: Iterator<Char> = head[index].lowerCases().iterator()

    fun nextPassword(): List<Char> {
        if (charIterator.hasNext()) {
            head[index] = charIterator.next()
        } else {
            while (index >= 0 && head[index] == 'z') {
                head[index] = 'a'
                index--
            }
            if (index < 0) {
                head = "aaaaaaaa".toArrayList()
            } else {
                head[index] = head[index].inc()
            }
            index = 7
            charIterator = head[index].lowerCases().iterator()
        }
        return head
    }
    return sequence { nextPassword() }
}


fun Char.lowerCases() : Sequence<Char> = if (this in 'a'..'z') {
    var head = this
    val stop = 'z'.inc()
    sequence { head = head.inc(); head }.takeWhile { it != stop }
} else emptySequence<Char>()

//@Test
//class Tests {
//
//    fun emptySeq()  = assertEquals(0, 'A'.lowerCases().count())
//    fun singleSeq() = assertEquals(listOf('z'), 'z'.lowerCases().toList())
//}