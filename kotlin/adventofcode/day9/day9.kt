package adventofcode.day9

import euler.iterators.permutations
import java.io.File
import java.util.*
val testing = false

fun main(args: Array<String>) {
    val file =
            if (testing) File("kotlin/adventofcode/day9/example.txt")
            else File("kotlin/adventofcode/day9/input.txt")
    var lines = file.readLines()
    lines.forEach {
        val words = it.splitToSequence(delimiters = " ")
        map.addPath(words.first(), words.elementAt(2), words.last().toInt())
    }

    println("Cities: ${map.names}")
    println("Distances:")
    map.dists.forEachIndexed { index, list -> println("$index. ${map.names[index]} : $list") }
    println()
    val time1 = System.currentTimeMillis()
    val result = map.searchMinPath()
    val time2 = System.currentTimeMillis()
    println("Simple algorithm")
    result.paths.forEachIndexed { index, path -> println("$index. $path") }

    println("Length: ${result.length} (found in ${time2 - time1} miliseconds)")

    var permutations = map.indices.toList().permutations()
    val resultBruteForce = permutations
            .map { permutation -> map.lengthOfPath(permutation) }
            .minBy { permutation -> permutation.length }
    val time3 = System.currentTimeMillis()
    println("Length: ${resultBruteForce!!.length} (found in ${time3 - time2} miliseconds)")



}
object map  {
    val size_input = if (testing) 4 else 8
    val indices = 0..size_input-1
    val dists: ArrayList<ArrayList<Int>> = indices.map {
        indices.map { 0 }.toArrayList()
    }.toArrayList()


    val cities = HashMap<String, Int>()
    val names = ArrayList<String>()
    var currentCity = 0

    fun addCity(name: String): Int {
        if (name in cities) {
            return cities[name] as Int
        } else {
            val result =currentCity++
            cities[name] = result
            names.add(name)
            return result
        }
    }
    fun addPath(city1: String, city2: String, length: Int) {
        val i = addCity(city1)
        val j = addCity(city2)
        dists[i][j] = length
        dists[j][i] = length
    }


    fun searchMinPath(end: Int = cities.size-1): PathResult {
        if (end == 0) {
            return PathResult(0, ArrayList())
        } else {
            var indexMax = -1 ; var distMax = Int.MAX_VALUE
            dists[end].forEachIndexed { index, dist ->
                if ((index < end) && (dist < distMax)) {
                    distMax = dist
                    indexMax = index
                }
            }
            var rec = searchMinPath(end = end -1)
            val minPath = Path(city1 = names[end], city2 = names[indexMax], length = distMax)
            return PathResult(rec.length+distMax, rec.paths +minPath)
        }
    }

    fun lengthOfPath(permutation: List<Int>): PathResult {
        val length = permutation.mapIndexed { index, city2 ->
            when(index) {
                0 -> 0
                else -> dists[permutation.get(index-1)][city2]
            }
        }.sum()
        return PathResult(length = length, permutation = permutation)

    }

}


data class PathResult(val length: Int, val paths: List<Path> = emptyList(), val permutation : List<Int> = emptyList())
data class Path(val city1: String, val city2: String, val length: Int) {
    override fun toString(): String = "D($city1, $city2)=$length"
}

