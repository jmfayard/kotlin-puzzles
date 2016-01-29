package adventofcode.day14

import adventofcode.day13.Timer
import org.testng.annotations.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

val testing = false
val filename = if (testing) "kotlin/adventofcode/day14/example.txt" else "kotlin/adventofcode/day14/input.txt"
val timeout = if (testing) 1000 else 2503

fun main(args: Array<String>) {
    val timer = Timer()

    val list = ArrayList<Reindeer>()
    File(filename).forEachLine { line ->
        val reinder = createReinder(line)
        list.add(reinder)
        println(reinder)
    }
    val runs = list.map { reinder -> reinder.startRunning() }
        .map { seq ->
            seq.elementAt(timeout)
        }
    list.zip(runs).forEach { pair ->
        println("Reinder ${pair.first.name} did run ${pair.second.lenght}")
    }
    println("Done ${timer.duration()}")

}


fun createReinder(line : String) : Reindeer {
    val l = line.splitToSequence(" ").toList()
    return Reindeer(name = l[0], speed = l[3].toInt(), active = l[6].toInt(), rest = l[l.size-2].toInt())
}
data class Reindeer (val name: String, val speed: Int, val active : Int, val rest : Int) {

    fun startRunning() : Sequence<Stat> {
        var head = Stat(0, 0)

        fun run1s() : Stat {
            val result = head
            head = when(head.seconds % (active + rest))
            {
                in 0..active-1 -> Stat(head.seconds+1, head.lenght + speed)
                else -> Stat(head.seconds+1, head.lenght)
            }
            return result
        }
        return sequence { run1s()  }
    }
}

data class Stat(val seconds : Int, val lenght: Int)

@Test
class Day14 {
    val comet = Reindeer("Comet", 14, 10, 127)

    fun createComet() = assertEquals(comet,
            createReinder("Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds."))

    fun runComet() {
        val stats = comet.startRunning().take(1002).toList()
        val times =    listOf(1, 10, 11, 138, 1000)
        val expected = listOf(14, 140, 140, 154, 1120)
        assertEquals(expected, times.map { stats[it].lenght })
    }
}
