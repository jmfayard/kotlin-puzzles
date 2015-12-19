package adventofcode.day6

import java.io.File
import java.util.*
import kotlin.text.MatchGroupCollection
import kotlin.text.MatchResult
import kotlin.text.Regex

val lights : MutableList<MutableList<Boolean>> = initMatrix()

fun main(args: Array<String>) {
    val file = File("/Users/jmfayard/Dev/perso/AdventOfCode/kotlin/adventofcode/day6/instructions.txt")
    file.forEachLine {
        if (regexp.matches(it)) {
            applyInstruction(it)
        }
    }

    println("count = ${lights.onCount()}")
//    applyInstruction("turn on 0,0 through 9,9")
//    applyInstruction("turn off 0,0 through 2,2")
//    applyInstruction("toggle 0,0 through 3,3")
//
//    //    applyInstruction("turn off 499,499 through 500,500")
////    applyInstruction("toggle 499,499 through 500,500")
//
//    //    println("count = ${lights.onCount()}")
////    applyInstruction("toggle 0,0 through 999,0")


    println(pointsInSquare( start=Point(x=0, y=0), end=Point(x=5, y=0)))


}

fun <E> MutableList<E>.onCount(): Int =
    pointsInSquare(Point(0,0), Point(999,999))
            .filter { p -> lights[p.x][p.y] == true }
            .count()


/**
turn on 461,734 through 524,991
toggle 206,824 through 976,912
turn on 826,610 through 879,892
 */
val regexp = Regex(pattern = "(turn on|toggle|turn off) (\\d+),(\\d+) through (\\d+),(\\d+)")

fun applyInstruction(instruction: String) {
    val groups = regexp.find(instruction)!!.groups
    val ints = groups.mapIndexed { i, it ->
                if (i in 2..5)
                    Integer.parseInt(it?.value)
                else
                    -1
            }
    val action = groups.get(1)?.value
    val start = Point(ints[2], ints[3])
    val end   = Point(ints[4], ints[5])
    when (action) {
        "toggle" -> toggleLights(start, end)
        "turn on" -> switchOnLights(start, end)
        "turn off" -> switchOffLights(start, end)
        else -> print("Unknown action ${action}")
    }
    println("instruction=${action} start=${start} end=${end} ON=${lights.onCount()}")

}
data class Point (val x : Int, val y : Int)

fun pointsInSquare(start: Point, end: Point): List<Point>
        = (start.x..end.x)
        .flatMap { x ->
            (start.y..end.y).map { y -> Point(x, y)
            }
        }

fun switchOffLights(start: Point, end: Point) =
        pointsInSquare(start, end)
        .forEach { p -> lights[p.x][p.y] = false }

fun switchOnLights(start: Point, end: Point) =
        pointsInSquare(start, end)
                .forEach { p -> lights[p.x][p.y] = true }

fun toggleLights(start: Point, end: Point) =
        pointsInSquare(start, end)
                .forEach { p -> lights[p.x][p.y] = !lights[p.x][p.y] }


fun initMatrix(): MutableList<MutableList<Boolean>> {
    return (0..999).map { (0..999).map { false }.toArrayList() }.toArrayList()
}
