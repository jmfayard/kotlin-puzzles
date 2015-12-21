package adventofcode.day7

import java.io.File
import java.util.*
import kotlin.text.Regex

infix fun Int.RSHIFT(bitCount: Int): Int = this shr bitCount
infix fun Int.LSHIFT(bitCount: Int): Int = this ushr bitCount
infix fun Int.AND(other: Int): Int = this and other
infix fun Int.OR(other: Int): Int = this or other
fun NOT(a: Int): Int = a.inv()


fun main(args: Array<String>) {
    val instructions = ArrayList<List<Lex>>()
    val testing = false
    val file = if (testing) File("/Users/jmfayard/Dev/perso/AdventOfCode/kotlin/adventofcode/day7/test.txt")
            else File("/Users/jmfayard/Dev/perso/AdventOfCode/kotlin/adventofcode/day7/circuit.txt")
    file.forEachLine {
        instructions.add(emitLine(it))
    }
    println("\n== Lexer ==")
    println(instructions.joinToString(separator = "\n"))
    val errors = validateInstructions(instructions)
    println("\n== Lexer Validation ==")
    if (errors.isEmpty()) println("No error found")
    else println(errors.joinToString(separator = "\n"))

    println("\n== Parser ==")
    println(buildCircuit(instructions).joinToString(separator = "\n"))

}

val circuit = object : Map<String, BitInstruction> by HashMap<String, BitInstruction>() {

}

fun buildCircuit(instructions: List<List<Lex>>): List<BitInstruction> {
    val result = instructions.map { instruction ->
        val last = instruction[instruction.lastIndex] as LexVariable
        if (instruction.size == 3) { // leaf
            val token = instruction[0] as LexExpression
            BitInstruction(
                    first = token,
                    second = null,
                    operator = null,
                    key = last.name)

        } else if (instruction.size == 4) {
            val token = instruction[1] as LexExpression
            BitInstruction(
                    first = token,
                    second = null,
                    operator = LexGate.NOT,
                    key = last.name)
        } else {
            val operator = instruction[1] as LexGate
            val firstToken = instruction[0] as LexExpression
            val secondToken = instruction[2] as LexExpression
            BitInstruction(
                    first = firstToken,
                    second = secondToken,
                    operator = operator,
                    key = last.name)
        }
    }
    return result
}

fun validateAndSortCircuit() {

}

fun Lex.tokenName(): String =
    if (this is LexVariable) name
    else ""


fun Lex.tokenValue(): Int? =
    if (this is LexValue) value
    else null

class BitInstruction(val key : String, val value: Int? = null, val operator: LexGate?, val first: LexExpression?, val second : LexExpression?) {
    fun isComputed() : Boolean = (value != null)
    fun dependancies(): List<String> {
        val result = ArrayList<String>()
        if (first != null && first is LexVariable)
            result.add(first.name)
        if (second != null && second is LexVariable)
            result.add(second.name)
        return result
    }

    override fun toString(): String = "BitInstruction( key=$key, value=$value, depandancies=${dependancies()} operator=$operator first=$first second=$second"
}


data class TokenError(val line : Int, val instruction : List<Lex>, val message : String)
fun validateInstructions(instructions: List<List<Lex>>) : List<TokenError> {
    val errors = ArrayList<TokenError>()

    instructions.forEachIndexed { line, instruction ->

        var errorMessage : String
        if (instruction.size < 3) {
            errorMessage = "No empty line allowed"
            return@forEachIndexed
        }
        val first = instruction[0]
        val second = instruction[1]
        val third = instruction[2]

        if (! (instruction[instruction.lastIndex] is LexVariable) && instruction[instruction.lastIndex-1] is LexAssignment)
            errorMessage = "Missing asignment"
        else
            errorMessage = when(instruction.size) {
                3 -> if (first is LexExpression) "" else "Invalid assignment"
                4 -> if (first == LexGate.NOT) "" else "Not an unary operator"
                5 -> if (second == LexGate.NOT) "Not an binary operator"
                    else if (!(first is LexExpression)) "Invalid first operand"
                    else if (!(third is LexExpression)) "Invalid second operand"
                    else ""
                else -> "No instruction are allowed with ${instruction.size} elements"
            }

        if (!errorMessage.isEmpty())
            errors.add(TokenError(line, instruction, errorMessage))
    }
    return errors
}

fun emitLine(line: String) : List<Lex> {
    println(line)
    return line.splitToSequence(" ").map { word ->
        when (word) {
            "->"  -> LexAssignment()
            "AND" -> LexGate.AND
            "OR" -> LexGate.OR
            "NOT" -> LexGate.NOT
            "LSHIFT" -> LexGate.LSHIFT
            "RSHIFT" -> LexGate.RSHIFT
            else ->
                if (isNumber(word))
                    LexValue(value = Integer.valueOf(word))
                else
                    LexVariable(name = word)
        }
    }.toList()
}

var regexp_isNumber = Regex(pattern = "[0-9]+")
fun isNumber(word: String): Boolean = regexp_isNumber.matches(word)



interface Lex {}
interface LexExpression : Lex
data class LexValue(val value: Int) : LexExpression
data class LexVariable(val name: String) : LexExpression
class LexAssignment() : Lex {
    override fun toString(): String = " -> "
}
enum class LexGate : Lex {
    AND, OR, NOT, LSHIFT, RSHIFT ;
}




