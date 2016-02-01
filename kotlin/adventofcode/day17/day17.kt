package adventofcode.day17

import adventofcode.day13.Timer
import euler.iterators.permutations
import org.testng.annotations.Test

val containers = listOf(50, 44, 11, 49, 42, 46, 18, 32, 26, 40, 21, 7, 18, 43, 10, 47, 36, 24, 22, 40)

fun main(args: Array<String>) {
    val timer = Timer()
    val result = containers.allArrangements()
        .filter { it.sum() == 150 }
        .count()
    println("Found $result in ${timer.duration()}")
}

fun <T> List<T>.allArrangements() : Sequence<List<T>> =
        if (isEmpty()) sequenceOf(emptyList())
        else  (this - first())
            .allArrangements()
            .flatMap { list -> sequenceOf(list, list + first()) }


@Test
class Day17 {
    val testContainers = listOf(20, 15, 10, 5, 5)

    fun test0(list : List<Int> = emptyList()) {
        println("All combinaisons of ${list.size} elements")
        var i = 0
        list.allArrangements().forEach { i++; println(it) }
        println("Found $i results")
        println()
    }
    fun test2() {
        test0(listOf(1, 2))
    }
    fun test3() {
        test0(listOf(1, 2, 3))
    }
    fun test5() {
        test0(testContainers)
    }
    fun example() {
        testContainers.allArrangements()
                .filter { it.sum() == 25 }
                .forEach { println(it) }
    }



}
