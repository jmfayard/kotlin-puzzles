package adventofcode.day7

import java.io.File
import java.util.*
import kotlin.text.Regex

/***
 * Day of Code 7
 *
 * See http://adventofcode.com/day/7
 *
 *
 *    This year, Santa brought little Bobby Tables a set of wires and bitwise logic gates!
 *    Unfortunately, little Bobby is a little under the recommended age range,
 *    and he needs help assembling the circuit.
 *
 *    Each wire has an identifier (some lowercase letters) and can carry a 16-bit signal (a number from 0 to 65535).
 *    A signal is provided to each wire by a gate, another wire, or some specific value.
 *    Each wire can only get a signal from one source, but can provide its signal to multiple destinations.
 *    A gate provides no signal until all of its inputs have a signal.
 *
 *    The included instructions booklet describes how to connect the parts together:
 *    x AND y -> z means to connect wires x and y to an AND gate, and then connect its output to wire z.
 *
 *    For example:
 *
 *    123 -> x means that the signal 123 is provided to wire x.
 *    x AND y -> z means that the bitwise AND of wire x and wire y is provided to wire z.
 *    p LSHIFT 2 -> q means that the value from wire p is left-shifted by 2 and then provided to wire q.
 *    NOT e -> f means that the bitwise complement of the value from wire e is provided to wire f.
 *
 *    Other possible gates include OR (bitwise OR) and RSHIFT (right-shift).
 *    If, for some reason, you'd like to emulate the circuit instead,
 *    almost all programming languages (for example, C, JavaScript, or Python) provide operators for these gates.
 *
 *    For example, here is a simple circuit:
 *
 *    123 -> x
 *    456 -> y
 *    x AND y -> d
 *    x OR y -> e
 *    x LSHIFT 2 -> f
 *    y RSHIFT 2 -> g
 *    NOT x -> h
 *    NOT y -> i
 *
 *    After it is run, these are the signals on the wires:
 *
 *    d: 72
 *    e: 507
 *    f: 492
 *    g: 114
 *    h: 65412
 *    i: 65079
 *    x: 123
 *    y: 456
 *    In little Bobby's kit's instructions booklet (provided as your puzzle input), what signal is ultimately provided to wire a?
 *
 *
 */


/** ENTRY POINT **/

fun main(args: Array<String>) {
    val instructions = ArrayList<List<Lex>>()
    val testing = false
    val file =
            if (testing) File("kotlin/adventofcode/day7/test.txt")
            else File("kotlin/adventofcode/day7/circuit.txt")

    file.forEachLine { line ->
        if (line.isNotBlank())
            instructions.add(emitLine(line.trim()))
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

    println("\n== Computing Values ==")
    circuit.computeValues()

    println("\n== Result ==")
    println(circuit.result!!.joinToString(separator = "\n"))

}

/** LEXER ***/

data class TokenError(val line : Int, val instruction : List<Lex>, val message : String)

interface Lex {}
interface LexExpression : Lex
data class LexValue(val value: Int) : LexExpression
data class LexVariable(val name: String) : LexExpression
class LexAssignment() : Lex {
    override fun toString(): String = " -> "
}
enum class LexGate : Lex {
    AND, OR, NOT, LSHIFT, RSHIFT, ASSIGN ;
}

var regexp_isNumber = Regex(pattern = "[0-9]+")
fun isNumber(word: String): Boolean = regexp_isNumber.matches(word)


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




/** PARSER **/


data class ParserError(val message: String)

class BitOperation(val key : String, var value: Int? = null, val operator: LexGate?, val first: LexExpression?, val second : LexExpression?, var order : Int = -1) {
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


class Circuit {
    val list : List<BitOperation>
    var result : List<BitOperation>? = null
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
                        operator = LexGate.ASSIGN,
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

    fun computeValues() {
        result = list.sortedBy { it.order }
        result!!.forEach { operation ->
            // we know for a fact that dependancies are already satisfied
            val firstOperand   = lazy {  tokenValue(operation.first) as Int }
            val secondOperand  = lazy {  tokenValue(operation.second) as Int }

            //AND, OR, NOT, LSHIFT, RSHIFT, DIRECT_ASSIGNMENT ;
            operation.value = when(operation.operator) {
                LexGate.ASSIGN -> firstOperand.value
                LexGate.NOT -> firstOperand.value.NOT()
                LexGate.LSHIFT -> firstOperand.value LSHIFT secondOperand.value
                LexGate.RSHIFT -> firstOperand.value RSHIFT secondOperand.value
                LexGate.AND -> firstOperand.value AND secondOperand.value
                LexGate.OR -> firstOperand.value OR secondOperand.value
                else -> throw RuntimeException("Unknown operator ${operation.operator}")
            }
        }
    }

    fun tokenValue(expression: LexExpression?): Int? = when (expression) {
        null -> null
        is LexValue -> expression.value
        is LexVariable -> map.getRaw(expression.name)?.value
        else -> null
    }
}


infix fun Int.RSHIFT(bitCount: Int): Int = this shr bitCount
infix fun Int.LSHIFT(bitCount: Int): Int = this shl bitCount
infix fun Int.AND(other: Int): Int = this and other
infix fun Int.OR(other: Int): Int = this or other
fun Int.NOT(): Int = this xor 0xFFFF
