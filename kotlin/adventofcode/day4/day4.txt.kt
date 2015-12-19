package adventofcode.day4

import java.security.MessageDigest

val input = "yzbqklnj"

fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    println("Mining, please wait a lot....")

    val secret = findSecretNumber()
    val end = System.currentTimeMillis()

    println("Found ${secret} after ${end-start} mili seconds")

    val result = Md5.encode(input + secret)

    println(result)

}

fun findSecretNumber() : Int {
    val md5 = MessageDigest.getInstance("MD5")
    val zero = 0.toByte()

    (0..700000)
        .map { (input+it).toByteArray() }
        .forEachIndexed { number, bytes ->
            if (number % 1000 == 0) print('.')
            md5.update(bytes)
            val digest = md5.digest()
            if (digest.get(0) == zero && digest.get(1) == zero) {
                val hex = Integer.toHexString(digest.get(2).toInt())
                if (hex.length == 1) {
                    return number
                }
            }

        }
    return  -1
}