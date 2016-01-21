package crackcode.chap1

import org.testng.Assert.*
import org.testng.annotations.DataProvider
import org.testng.annotations.Parameters
import org.testng.annotations.Test

@Parameters("2")
class P00_Sum(val base: Int = 0) {


    fun sum(a : Int, b: Int) : Int = a+b+base

    @Test fun testSum() = assertEquals(sum(2,2), 4)

    @Test fun badTest() = assertEquals(sum(3,3), 6)
}
