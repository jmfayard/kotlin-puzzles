package crackcode.chap1

import org.testng.annotations.Test
import kotlin.test.assertEquals


fun reverseC(s : Sequence<Char>) : String {
    var result = s.takeWhile { it != '0' }.toList().asReversed() as MutableList
    return (result + '0').joinToString(separator = "")
}

@Test
class TestsP12 {

    fun test1() {
        val test = "abcd0fdsfs".asSequence()
        assertEquals("dcba0", reverseC(test))
    }

}



