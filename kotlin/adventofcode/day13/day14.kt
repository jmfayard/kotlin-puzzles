package adventofcode.day13

import euler.iterators.permutations
import java.io.File
import java.util.*

data class Configuration(val filename: String, val names : List<String>) {
    val relations: ArrayList<ArrayList<Int>> = names.indices.map {
        names.indices.map { 0 }.toArrayList()
    }.toArrayList()

    fun index(name: String) = names.indexOf(name)

    fun permutations() = names.indices.toList().permutations()

    fun happinness(permutation: List<Int>): Int {
        check(permutation.size == names.size)
        val rotate = permutation.mapIndexed { index: Int, value: Int ->
            permutation[if (index == permutation.lastIndex) 0 else index+1] }
        val sum = permutation.zip(rotate).sumBy { pair ->
            relations[pair.first][pair.second] + relations[pair.second][pair.first]
        }
        return sum
    }

}

val exampleConfig = Configuration("example.txt", listOf("Alice", "Bob", "Carol", "David"))
val realConfig = Configuration("input.txt", listOf("Alice", "Bob", "Carol", "David", "Eric", "Frank", "George", "Mallory"))

fun main(args: Array<String>) {
    val testing = false
    val config = if (testing) exampleConfig else realConfig
    val file = File("kotlin/adventofcode/day13/${config.filename}")
    file.forEachLine { line: String ->
        val list = line.splitToSequence(' ').asIterable().toList()
        val p1 = config.index(list.get(0))
        val p2 = config.index(list.last().replace(".", ""))
        val happiness = Integer.valueOf(list.get(3)) * if (list.get(2) == "lose") -1 else 1
        config.relations[p1][p2] = happiness
    }
    println(config.relations)

    val timer = Timer()
    val max = config.permutations().maxBy { order ->
        config.happinness(order)
    } as List<Int>

    val happinness = config.happinness(max)
    val positions = max.map { i -> config.names[i] }
    println("happinness=$happinness for positions = $positions ${timer.duration()}")

}

class Timer() {
    var start = System.currentTimeMillis()
    fun duration() : String = "(${System.currentTimeMillis() - start} ms)"
}

