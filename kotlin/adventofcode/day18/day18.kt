package adventofcode.day18

import adventofcode.day13.MyTimer
import adventofcode.day6.lights
import euler.iterators.range
import euler.iterators.times
import org.testng.annotations.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

val testInput = readInput(6, "testinput.txt")
val realInput = readInput(100, "input.txt")

fun main(args: Array<String>) {
    val timer = MyTimer()
    val iterations = 100
    (1..iterations).forEach { realInput.moveNext() }
    println(realInput)
    println()
    println("Still ${realInput.lightsOn()} lights on after $iterations iterations $timer")
}

data class Lights(val size: Int, val input : List<String>) {
    var lights : List<List<Boolean>> = parseInput()
    var turn = 0
    val range = 0..size-1

    fun moveNext() {
        var nextLights = ArrayList<ArrayList<Boolean>>()
        for (row in range) {
            val array = ArrayList<Boolean>()
            nextLights.add(array)
            for (col in range) {
                val isOn = lights[row][col]
                val lightsFromNeighbors = neighbors(row, col)
                        .sumBy { val (r, c) = it
                            if (lights[r][c]) 1 else 0
                        }
                array.add(when (lightsFromNeighbors) {
                    3 -> true
                    2 -> isOn
                    else -> false
                })
            }
        }
        turn++
        lights = nextLights
    }

    fun neighbors(row: Int, col: Int) : List<Pair<Int, Int>> {
        check(row in range && col in range) {  "Illegal parameters ($row, $col)" }
        val rows = listOf(row-1, row, row+1)
        val cols = listOf(col-1, col, col+1)
        return  (rows * cols)
                .filter { it ->
                    val (r, c) = it
                    r != row || c != col
                }.filter { val (r, c) = it
                    r >= 0 && r < size && c >= 0 && c < size
                }.toList()
    }


    fun reset() : Unit {
        turn = 0
        lights = parseInput()
    }
    private fun parseInput(): List<List<Boolean>> {
        return input.take(size).map { line ->
            line.take(size).map { c: Char ->
                when (c) {
                    '.' -> false
                    '#' -> true
                    else -> throw IllegalArgumentException("Unexpected $c in line $line")
                }
            }
        }
    }

    fun lightsOn() : Int =
        lights.sumBy { r -> r.sumBy { if (it) 1 else 0 } }


    override fun toString(): String =
            range.map { row ->
                range.map { col -> if (lights[row][col]) '#' else '.' }.joinToString(separator = "")
            }.joinToString(separator = "\n")

}


fun readInput(i: Int, path: String): Lights {
    val file = File("kotlin/adventofcode/day18/$path")
    if (file.canRead()) {
        return Lights(i, file.readLines())
    } else {
        throw IllegalArgumentException("File $path is not readable")
    }
}





@Test
class Day18 {
    fun print() {
        println(testInput)
    }
    fun productOfLists() {
        val a = listOf(1, 2, 3)
        val b = listOf("a", "b", "c")
        println((a * b).toList())
    }
    fun neighbors() {
        fun test(first: Int, second: Int, size: Int) {
            print("Neighbors($first, $second): ")
            val neighbors = testInput.neighbors(first, second)
            println(neighbors.joinToString(separator = " "))
            assertEquals(size, neighbors.size)
        }
        test(0, 0, 3)
        test(2, 2, 8)
        test(0, 3, 5)
        test(1, 0, 5)
        test(5, 0, 3)
        test(5, 2, 5)
        test(5, 5, 3)

    }
    fun parseInput() {
        val lights = testInput.lights
        println(lights)
        assertEquals(testInput.size, lights.size)
        assert(lights.all { r -> r.size == lights.size })
        assertEquals(15,  testInput.lightsOn())
    }
    fun moves() {
        println("After 0 steps")
        println(testInput)
        (1..4).forEach { it ->
            testInput.moveNext()
            println("After $it steps")
            println(testInput)
            println()
        }
        assertEquals(4, testInput.lightsOn())

        testInput.reset()
    }
}