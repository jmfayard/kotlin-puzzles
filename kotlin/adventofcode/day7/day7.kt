package adventofcode.day7

import sun.tools.java.CompilerError
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
    val lex_errors = validateInstructions(instructions)

    println("\n== Lexer Validation ==")
    if (lex_errors.isEmpty()) println("No error found")
    else {
        println(lex_errors.joinToString(separator = "\n"))
        return
    }

    println("\n== Parser ==")
    val circuit = Circuit(instructions)
    println(circuit.list.joinToString(separator = "\n"))

    println("\n== Parser Validation ==")
    var errors: List<ParserError>
    errors = circuit.searchDuplicates()
    if (errors.isNotEmpty()) {
        println(errors.joinToString(separator = "\n"))
        return
    }
    errors = circuit.sortAcyclicGraph()
    if (errors.isNotEmpty()) {
        println(errors.joinToString(separator = "\n"))
        return
    }

    println("\n== Result ==")
    println(circuit.list.joinToString(separator = "\n"))

}

class Circuit {
    val list : List<BitOperation>
    val map  : Map<String, BitOperation>
    val keys : List<String>

    constructor(instructionsList: List<List<Lex>>) {
        list = instructionsList.map { instructions ->
            val last = instructions.last() as LexVariable
            if (instructions.size == 3) { // leaf
                val token = instructions[0] as LexExpression
                BitOperation(
                        first = token,
                        second = null,
                        operator = null,
                        key = last.name)

            } else if (instructions.size == 4) {
                val token = instructions[1] as LexExpression
                BitOperation(
                        first = token,
                        second = null,
                        operator = LexGate.NOT,
                        key = last.name)
            } else {
                val operator = instructions[1] as LexGate
                val firstToken = instructions[0] as LexExpression
                val secondToken = instructions[2] as LexExpression
                BitOperation(
                        first = firstToken,
                        second = secondToken,
                        operator = operator,
                        key = last.name)
            }
        }

        keys = list.map { it.key }
        map = list.toMapBy { it.key }
    }

    fun searchDuplicates() : List<ParserError> = list
            .groupBy { it.key }
            .filter { entry -> entry.value.size != 1 }
            .map { entry ->
                ParserError("Duplicate assignment for variable=${entry.key}")
            }

    fun sortAcyclicGraph() : List<ParserError> {
        var sorted = 0
        val errors = ArrayList<ParserError>()


        // DEPTH-FIRST
        while (sorted < list.size) {
            val previouslySorted = sorted

            list.forEach { operation ->

                // Skip if dependancy not satisfied
                val deps: List<BitOperation?> = operation.dependancies().map { map[it] }
                if (operation.alreadyOrdered()) {
                    // nothing to do
                    return@forEach
                } else if (deps.all { it?.alreadyOrdered() == true }) {
                        operation.order = sorted
                        sorted++

                } else if (deps.any { it == null }) {
                    errors.add(ParserError("Unsatisfied depdancy for operation=$operation"))

                } else {
                    // dependancy not satisfied yet, hopefully in the next while()
                    return@forEach
                }
            }

            if (sorted == previouslySorted) {
                errors.add(ParserError("Acyclic graph, size=${list.size} sorted=${previouslySorted}"))
                return errors

            }
        }
        return errors
    }

}

data class ParserError(val message: String)


fun Lex.tokenName(): String =
    if (this is LexVariable) name
    else ""


fun Lex.tokenValue(): Int? =
    if (this is LexValue) value
    else null

class BitOperation(val key : String, var value: Int? = null, val operator: LexGate?, val first: LexExpression?, val second : LexExpression?, var order : Int = -1) {
    fun isComputed() : Boolean = (value != null)
    fun dependancies(): List<String> {
        val result = ArrayList<String>()
        if (first != null && first is LexVariable)
            result.add(first.name)
        if (second != null && second is LexVariable)
            result.add(second.name)
        return result
    }

    override fun toString(): String = "BitInstruction( key=$key, value=$value, depandancies=${dependancies()} operator=$operator first=$first second=$second order=${order}"

    fun alreadyOrdered(): Boolean = (order != -1)
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

        if (! (instruction.last() is LexVariable) && instruction[instruction.lastIndex-1] is LexAssignment)
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




