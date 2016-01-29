package adventofcode.day15

import org.testng.annotations.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFails

val testing = true


val testingInput = """Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3"""
val realInput = """Frosting: capacity 4, durability -2, flavor 0, texture 0, calories 5
Candy: capacity 0, durability 5, flavor -1, texture 0, calories 8
Butterscotch: capacity -1, durability 0, flavor 5, texture 0, calories 6
Sugar: capacity 0, durability 0, flavor -2, texture 2, calories 1"""

val input = if (testing) testingInput else realInput

fun main(args: Array<String>) {
    val ingredients =  createIngredients(realInput)
    val bestcookie: Cookie? = allProportions()
            .map { proportion -> Cookie(ingredients, proportion) }
            .maxBy { cookie -> cookie.totalscore() }
    println("Best Cookie found: $bestcookie}")
    println("Total score: ${bestcookie?.totalscore()}")

}

fun allProportions() : Sequence<List<Int>> {
    val range = 0..100
    val result = ArrayList<List<Int>>()
    for (i in range) {
        for (j in range) {
            for (k in range) {
                if (i+j+k <= 100) {
                    result.add(listOf(i, j, k, 100 - i - j - k))
                }
            }
        }
    }
    return result.asSequence()
}

fun createIngredients(input : String) : List<Ingredient> = input.splitToSequence('\n')
        .map { createIngredient(it) }
        .filterNotNull()
        .toList()

fun createIngredient(line : String) : Ingredient? {
    val list = line.splitToSequence(' ', ':', ',').toList()
    if (list.size < 16) return null

    val tokens = listOf(3, 6, 9, 12, 15).map { list.get(it).toInt() }
    return Ingredient(list.first(), tokens[0], tokens[1], tokens[2], tokens[3], tokens[4])
}

data class Ingredient(val name: String, val capacity: Int, val durability: Int, val flavor : Int, val texture: Int, val calories : Int)

data class Cookie(val ingredients: List<Ingredient>, val proportions : List<Int>) {
    init {
        check(ingredients.size == proportions.size)
        check(proportions.sum() == 100)
    }
    private fun weightedSum(transform : Ingredient.() -> Int) : Int
            = ingredients.zip(proportions)
            .sumBy { pair -> pair.first.transform() * pair.second}

    fun capacity(): Int = weightedSum {  capacity }
    fun durability(): Int= weightedSum { durability }
    fun flavor(): Int = weightedSum { flavor }
    fun texture(): Int = weightedSum { texture }
    fun totalscore(): Int {
        val properties = listOf(capacity(), durability(), flavor(), texture())
        if (properties.any { it <= 0 })
            return 0
        else
            return properties.reduce { a, b -> a*b }
    }

    override fun toString(): String = "Cookie(ingredients=${ingredients.map { it.name }}, propotions=$proportions)"


}

@Test
class Day15 {
    public val candy = Ingredient("Candy", 0, 5, -1, 0, 8)

    fun noNegativeCookie() {
        val chocolate = candy.copy(name = "Chocolate", flavor = -2 )
        val cookie = Cookie(
                ingredients = listOf(candy, chocolate),
                proportions = listOf(50, 50))
        assert(cookie.flavor() < 0)
        assertEquals(0, cookie.totalscore())
    }
    fun createCookie() {
        val ingredients = createIngredients(testingInput)
        assertFails { Cookie(ingredients, listOf(23)) }
        assertFails { Cookie(ingredients, listOf(44, 50)) } // sum is not 100
        val cookie = Cookie(ingredients, listOf(44, 56))
        val expected = listOf(68, 80, 152, 76, 62842880)
        val actual = listOf<Int>(cookie.capacity(), cookie.durability(), cookie.flavor(), cookie.texture(), cookie.totalscore())
        assertEquals(expected, actual)

    }
    fun createCandy() = assertEquals(candy,
            createIngredient("Candy: capacity 0, durability 5, flavor -1, texture 0, calories 8"))
    fun size() = assertEquals(if (testing) 2 else 4,
            input.split("\n").count())
}