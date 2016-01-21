package euler.problem002

import euler.bigInt
import euler.iterators.fibonacci
import java.math.BigInteger


fun main(args: Array<String>) {
    val max = bigInt(1000)

//    fibonacci().takeWhile { it < max }.filterNotNull().filter { it -> it % 2 == 0 }

}

fun pb2(max: BigInteger): BigInteger = fibonacci().fold(bigInt(0)) { accu, fibo ->
        if (fibo.compareTo(max) > 0)
            return accu
        else if (fibo.mod(bigInt(2)).equals(1))
            accu.add(fibo)
        else
            accu

    }
