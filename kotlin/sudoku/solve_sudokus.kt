package sudoku

fun main(args: Array<String>) {
    println("squares=$squares")
    println("unitlist=$unitlist")
    println("units=$units")
    println("peers=${peers["C2"]}")
}

/**
 * [A cross B] returns a [List] cross products of the elements in A and the elements in B
 */
infix fun List<String>.cross(list: List<String>): List<String>  =
        this.flatMap { i -> list.map { j -> "$i$j" } }

fun String.chars(): List<String> = this.map(Char::toString)

val rows =   "ABCDEFGHI".chars()
val cols =   "123456789".chars()
val unitsColumns = cols.map { c -> (rows cross listOf(c)) }
val unitsRows = rows.map { r -> (listOf(r) cross cols) }
val units3x3 = listOf("ABC","DEF","GHI").flatMap { a -> listOf("123","456","789").map { b -> a.chars() cross b.chars() } }
val unitlist =  (unitsColumns  + unitsRows + units3x3)
val squares = rows cross cols

val units = squares.toMap({ u -> u }, { u -> unitlist.filter { it.contains(u) } })
val peers = squares.toMap({ u -> u},  { u -> units[u]!!.flatMap { it }.distinct().minus(u) })



