package euler

import java.io.File
import java.math.BigInteger
import java.util.*
import kotlin.math.times

import java.util.ArrayList
import java.util.LinkedList

import euler.iterators.zipWith3

// candidates for kotlin
inline fun Any.toCharList() = toString().toArrayList()
inline fun Any.toDigits() = toCharList().map { c: Char -> Character.getNumericValue(c) }
inline fun Any.isPalindrome() = toCharList() == toCharList().reversed()

// candidates for kotlin.math
inline fun <T: Number> bigInt(n: T) : BigInteger = BigInteger(n.toString())

fun factorial(n: Int, product: BigInteger = bigInt(1)): BigInteger = if (n == 0) product else factorial(n - 1, n * product)
inline operator fun Int.times(multiplicand: BigInteger) = bigInt(this) * multiplicand

inline fun Int.multipleOf(n: Int) = toLong() multipleOf n
inline infix fun Long.multipleOf(n: Int) = this % n == 0.toLong()
inline fun BigInteger.multipleOf(n: Int) = this % bigInt(n) == bigInt(0)

inline fun Int.isPrime() = toLong().isPrime()
inline fun Long.isPrime() = this > 1 && smallestPrimeFactor() == null
inline fun Long.isEven() = (this % 2) == 0L

inline fun Int.numberOfDivisors(): Int = toLong().numberOfDivisors()
inline fun Long.numberOfDivisors(): Int {
    return primeFactors(this).groupBy { it }.values.map { group: List<Long> -> group.size + 1 }.product()
}

fun primeFactors(n: Long): List<Long> {
    val primeFactor = n.smallestPrimeFactor()
    return if (primeFactor == null) listOf(n) else primeFactor + primeFactors(n / primeFactor)
}

inline fun Int.smallestPrimeFactor() = toLong().smallestPrimeFactor()?.toInt()
inline fun Long.smallestPrimeFactor() = (2..Math.sqrt(toDouble()).toLong()).find { this % it == 0.toLong() }

// candidates for JavaIterables.kt


// sum
inline fun Array<BigInteger>.sum() : BigInteger = fold<BigInteger, BigInteger>(bigInt(0)) { a : BigInteger, b: BigInteger -> a.add(b) }
inline fun Iterable<BigInteger>.sum() = fold<BigInteger, BigInteger>(bigInt(0)) { a, b -> a.add(b) }
inline fun Sequence<BigInteger>.sum() = fold<BigInteger, BigInteger>(bigInt(0)) { a, b -> a.add(b) }

// product
inline fun Array<Int>.product() = fold(1) { a, b: Int -> a * b }
inline fun Array<Float>.product() = fold(1.toFloat()) { a, b: Float -> a * b }
inline fun Array<Double>.product() = fold(1.toDouble()) { a, b: Double -> a * b }
inline fun Array<Long>.product() = fold(1.toLong()) { a, b: Long -> a * b }
inline fun Array<BigInteger>.product() = fold(bigInt(1)) { a, b: BigInteger -> a * b }

inline fun Iterable<Int>.product() = fold(1) { a, b: Int -> a * b }
inline fun Iterable<Float>.product() = fold(1.toFloat()) { a, b: Float -> a * b }
inline fun Iterable<Double>.product() = fold(1.toDouble()) { a, b: Double -> a * b }
inline fun Iterable<Long>.product() = fold(1.toLong()) { a, b: Long -> a * b }
inline fun Iterable<BigInteger>.product() = fold(bigInt(1)) { a, b: BigInteger -> a * b }

inline fun Sequence<Int>.product() = fold(1) { a, b -> a * b }
inline fun Sequence<Float>.product() = fold(1.toFloat()) { a, b -> a * b }
inline fun Sequence<Double>.product() = fold(1.toDouble()) { a, b -> a * b }
inline fun Sequence<Long>.product() = fold(1.toLong()) { a, b -> a * b }
inline fun Sequence<BigInteger>.product() = fold(bigInt(1)) { a, b -> a * b }

// cartesian products

fun <T: Any> Iterable<T>.findTriplet(predicate: (T, T, T) -> Boolean): Triple<T, T, T>? {
    for (a in this) for (b in this) for (c in this) if ((predicate)(a, b, c)) return Triple(a, b, c)
    return null
}


fun <A : Any, B : Any, C : Any, D : Any> zipWith3(f: (A, B, C) -> D,
                                                  iterable1: Iterable<A>,
                                                  iterable2: Iterable<B>,
                                                  iterable3: Iterable<C>): List<D> {
    return zipWith3(f, iterable1.asSequence(), iterable2.asSequence(), iterable3.asSequence()).toList()
}

/**
 * Returns a list starting with the *initial* value followed by the intermediary result of reducing sequential pairs of elements from left to right
 */
fun <T : Any> Iterable<T>.scanLeft(initial: T, operation: (T, T) -> T): List<T> {
    val result = arrayListOf(initial)
    forEach { result.add(operation(result.last(), it)) }
    return result
}

// candidates for kotlin.util

inline fun <T: Any> List<T>.rotations(): List<List<T>> {
    val result = arrayListOf<List<T>>(this)
    val linkedList = LinkedList(this)
    (2..size).forEach {
        linkedList.addLast(linkedList.removeFirst())
        result.add(ArrayList(linkedList))
    }
    return result
}

inline operator fun <T: Any> T.plus(list: List<T>): List<T> {
    val copy = ArrayList(list)
    copy.add(0, this)
    return copy
}

// miscellaneous

fun loadRowsFrom(file: File): List<List<Int>> {
    val rows = ArrayList<List<Int>>()
    file.forEachLine(charset = "UTF-8") { rows.add(it.split(" ").map { it.toInt() }) }
    return rows
}

fun <T : Any> T.replicate(n: Int): List<T> {
    val result = ArrayList<T>(n)
    (1..n).forEach { result.add(this) }
    return result
}