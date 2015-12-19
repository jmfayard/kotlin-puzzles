package adventofcode.day2

import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.text.MatchGroupCollection
import kotlin.text.Regex

fun main(args: Array<String>) {
    val input = File("/Users/jmfayard/Dev/perso/AdventOfCode/kotlin/adventofcode/day2/presents.txt")
    val presents = ArrayList<Present>()

    val regexp = Regex(pattern = "(\\d+)x(\\d+)x(\\d+)")

    val present = regexp.toPresent("2x3x4")
    println("Taille papier cadeau: ${present?.taillePapier()}")
    println(present)
    input.forEachLine {
        val present = regexp.toPresent(it)
//        println("Taille papier cadeau: ${present?.taillePapier()}")
        if (present != null) presents.add(present)
    }
    val tailleTotalPapier = presents.map { it.taillePapier() }.sum()
    println("Hé les elfes, commandez ${tailleTotalPapier} cm^2 de papier")
}

data class Present(val length: Int, val width: Int, val height: Int) {
    fun taillePapier(): Int {
        val facesDuCadeau = listOf<Int>(length * width, width * height, height * length)
        val laPlusPetite: Int = facesDuCadeau.min()!!
        return  2* length * width + 2 * width * height + 2  * height * length + laPlusPetite
    }
}



fun Regex.toPresent(input : String) : Present? {
    if (this.matches(input)) {
        val groups: List<Int> = matchEntire(input)!!.groups.mapIndexed { i, group ->
            if (i==0) 0
            else Integer.parseInt(group?.value)
        }
        return Present(groups[1], groups[2], groups[3])
    } else {
        return null
    }

}


