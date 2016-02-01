package crackcode.chap1

import org.testng.annotations.Test
import kotlin.test.assertEquals

fun uniqChars(s : String) : Boolean = s.count() == s.toArrayList().distinct().count()

fun check(s: String, expected: Boolean) = assertEquals(uniqChars(s), expected, "Test failed for uniqChars($s)")
@Test
class P11 {

    fun same() = check("abcdef", true)
    fun empty() = check("", true)
    fun doublons() = check("abc0defghijk0lmn", false)

}