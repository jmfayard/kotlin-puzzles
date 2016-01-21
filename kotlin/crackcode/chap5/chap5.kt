package crackcode.chap5

import org.testng.annotations.*
import kotlin.test.assertEquals

fun main(args: Array<String>) {

}

@Test
class P5_0_Examples {

    fun minus() = assertEquals(0b1010-0b0001, 0b1001)   // 1010 - 0001
    fun plus() = assertEquals(0b1010 + 0b0110, 0b10000) // 1010 + 0110
    fun and() = assertEquals(0b1100 and 0b1010, 0b1000) // 1100^1010
    fun and2() = assertEquals(0b1001 and 0b1001,9) //1001^1001
    fun left() = assertEquals(0b1010 shl 1, 0b10100)    // 1010 << 1
    fun left2() = assertEquals(0b1010 shl 1, 20)    // 1010 << 1
    fun right() = assertEquals(0b1010 shr 1, 0b101) //1010 >> 1
    fun hex1() = assertEquals(0xAB + 0x11, 0xBC)
    fun hex2() = assertEquals(0xFF - 1, 0xFE)
}
/**
1010 - 0001
1010 + 0110
1100^1010
1010 << 1
1001^1001
1001 & 1100
1010 >> 1
0xFF - 1
0xAB + 0x11
 **/