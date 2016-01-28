package adventofcode.day10

var testing = false
val input = if (testing) "1" else "1321131112"
val iterations = if (testing) 5 else 40

fun main(args: Array<String>) {
    if (testing)
        input.readOutLoud().take(iterations).forEach { println(it) }

    val result = input.readOutLoud().take(iterations).last()
    println("${result.length}")

}

fun String.readOutLoud() : Sequence<String> {
    var head = this

    fun nextString() : String {
        val result = StringBuilder()
        val iterator = head.iterator()
        var current = iterator.next() to 1
        while (iterator.hasNext()) {
            val c = iterator.next()
            if (c != current.first) {
                result.append(current.second)
                result.append(current.first)
                current = c to 1
            } else {
                current = current.copy(second = current.second+1)
            }
        }
        result.append(current.second)
        result.append(current.first)
        head = result.toString()
        return head
    }

    return sequence { nextString() }
}
