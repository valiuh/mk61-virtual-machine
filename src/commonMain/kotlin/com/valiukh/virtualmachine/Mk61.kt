package com.valiukh.virtualmachine

import kotlin.math.PI as KOTLIN_PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.truncate
import kotlin.random.Random

/**
 * A class representing the Mk61 virtual machine.
 */
class Mk61 {

    companion object {
        const val PI: Double = 3.14159265358979323846
        const val E: Double = 2.71828182845904523536
    }

    private var acc = 0
    private var x1 = 0.0
    private val prng = Random(seed = 0)

    private var registersData = hashMapOf(
        Registers.RG0.registerName to 0.0,
        Registers.RG1.registerName to 0.0,
        Registers.RG2.registerName to 0.0,
        Registers.RG3.registerName to 0.0,
        Registers.RG4.registerName to 0.0,
        Registers.RG5.registerName to 0.0,
        Registers.RG6.registerName to 0.0,
        Registers.RG7.registerName to 0.0,
        Registers.RG8.registerName to 0.0,
        Registers.RG9.registerName to 0.0,
        Registers.RGA.registerName to 0.0,
        Registers.RGB.registerName to 0.0,
        Registers.RGC.registerName to 0.0,
        Registers.RGD.registerName to 0.0,
        Registers.RGE.registerName to 0.0,
    )

    private val registersStack = MutableList<Double>(4) { 0.0 }
    private val commandsStack = MutableList<Command>(1) { Command.NOP }

    private val subprogramStack = ArrayDeque<Int>(5)

    private val programMemory = ArrayList<String>()

    /**
     * Uploads a program to the virtual machine.
     *
     * @param input The program input as a string.
     */
    fun uploadProgram(input: String) {
        acc = 0
        programMemory.clear()
        val terms = input.split("\n", ";")
        terms.forEach { programMemory.add(it.trim()) }
    }

    /**
     * Executes the loaded program.
     */
    fun calculate() {
        while (true) {
            val command = programMemory[acc]
            if(command.isEmpty()) break
            if (command == Command.STOP.commandMnemonics) {
                break
            } else {
                parse(command)
            }
        }
    }


    /**
     * Returns the value of the X register.
     *
     * @return The value of the X register.
     */
    fun printX(): Double {
        return registersStack[0]
    }

    /**
     * Returns the value of the X1 register.
     *
     * @return The value of the X1 register.
     */
    fun printX1(): Double {
        return x1
    }

    /**
     * Returns the values of the registers stack.
     *
     * @return A list of values in the registers stack.
     */
    fun printRegisters(): List<Double> {
        return registersStack
    }

    /**
     * Returns the value of a specified data register.
     *
     * @param register The name of the register.
     * @return The value of the specified register, or null if the register does not exist.
     */
    fun printDataRegister(register: String): Double? =
        registersData[register]

    private fun parse(term: String) {
        if (commandsStack[0] != Command.NOP) {
            performControlCommand(commandsStack[0], term)
            commandsStack[0] = Command.NOP
        } else {
            if (term.isNumber()) {
                registersStack[0] = term.toDouble()
                increaseAccumulator()
            } else {
                if (term.hasMemoryCommand()) {
                    val parts = term.substringBeforeLast(" ") to term.substringAfterLast(" ")
                    val commandMnemonics = parts.first
                    val commandData = parts.second
                    Command.values().find { it.commandMnemonics == commandMnemonics }?.let { command ->
                        performMemoryCommand(command, commandData)
                    }
                } else {
                    val command = Command.values().find { it.commandMnemonics == term }
                    if(command != null) {
                        if (command.isControlCommand()) {
                            commandsStack[0] = command
                            increaseAccumulator()
                        }  else {
                            performArithmeticalOperation(command)
                        }
                    } else {
                        throw Exception ("Unknown operation Exception")
                    }
                }
            }
        }
    }

    private fun performArithmeticalOperation(command: Command) {
        when (command) {
            Command.NOP -> {}

            Command.CLR -> {
                registersStack[0] = 0.0
            }

            Command.PUSH -> {
                registersStack.push()
            }

            Command.SHIFT -> {
                x1 = getXRegister()
                registersStack.shift()
            }

            Command.SWAP -> {
                x1 = registersStack[0]
                registersStack.swap()
            }

            Command.ADD -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[1] + registersStack[0]
                registersStack.shiftLeft()
            }

            Command.SUB -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[1] - registersStack[0]
                registersStack.shiftLeft()
            }

            Command.MUL -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[1] * registersStack[0]
                registersStack.shiftLeft()
            }

            Command.DIV -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[1] / registersStack[0]
                registersStack.shiftLeft()
            }

            Command.EXP10 -> {
                x1 = registersStack[0]
                registersStack[0] = 10.0.pow(registersStack[0])
            }

            Command.EXP -> {
                x1 = registersStack[0]
                registersStack[0] = E.pow(registersStack[0])
            }

            Command.LG -> {
                x1 = registersStack[0]
                registersStack[0] = log10(registersStack[0])
            }

            Command.NL -> {
                x1 = registersStack[0]
                registersStack[0] = ln(registersStack[0])
            }

            Command.SIN -> {
                x1 = registersStack[0]
                registersStack[0] = sin(registersStack[0].toRadians())
            }

            Command.COS -> {
                x1 = registersStack[0]
                registersStack[0] = cos(registersStack[0].toRadians())
            }

            Command.TG -> {
                x1 = registersStack[0]
                registersStack[0] = tan(registersStack[0].toRadians())
            }

            Command.ARCSIN -> {
                x1 = registersStack[0]
                registersStack[0] = asin(registersStack[0]).toDegrees()
            }

            Command.ARCCOS -> {
                x1 = registersStack[0]
                registersStack[0] = acos(registersStack[0]).toDegrees()
            }

            Command.ARCTG -> {
                x1 = registersStack[0]
                registersStack[0] = atan(registersStack[0]).toDegrees()
            }

            Command.SQR -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[0].pow(2.0)
            }

            Command.SQRT -> {
                x1 = registersStack[0]
                registersStack[0] = sqrt(registersStack[0])
            }

            Command.FRAC -> {
                x1 = registersStack[0]
                registersStack[0] = 1 / registersStack[0]
            }

            Command.POW -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[0].pow(registersStack[1])
            }

            Command.PI -> {
                registersStack[0] = PI
            }

            Command.E -> {
                registersStack[0] = E
            }

            Command.FLR -> {
                x1 = registersStack[0]
                registersStack[0] = truncate(registersStack[0])
            }

            Command.FRC -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[0] % 1
            }

            Command.ABS -> {
                x1 = registersStack[0]
                registersStack[0] = abs(registersStack[0])
            }

            Command.MAX -> {
                x1 = registersStack[0]
                registersStack[0] =
                    if (registersStack[0] >= registersStack[1])
                        registersStack[0]
                    else
                        registersStack[1]
            }

            Command.SIGN -> {
                x1 = registersStack[0]
                registersStack[0] =
                    if (registersStack[0] > 0)
                        1.0
                    else if (registersStack[0] < 0)
                        -1.0
                    else
                        0.0
            }

            Command.AND -> {
                x1 = registersStack[0]
                val a = registersStack[0].toInt()
                val b = registersStack[1].toInt()
                registersStack[0] = (a and b).toDouble()
            }

            Command.OR -> {
                x1 = registersStack[0]
                val a = registersStack[0].toInt()
                val b = registersStack[1].toInt()
                registersStack[0] = (a or b).toDouble()
            }

            Command.XOR -> {
                x1 = registersStack[0]
                val a = registersStack[0].toInt()
                val b = registersStack[1].toInt()
                registersStack[0] = (a xor b).toDouble()
            }

            Command.NOT -> {
                x1 = registersStack[0]
                registersStack[0] = registersStack[0].toInt().inv().toDouble()
            }

            Command.LASTX -> {
                val lastX = x1
                x1 = registersStack[0]
                registersStack.push()
                registersStack[0] = lastX
            }

            Command.HM_TO_DEG -> {
                x1 = registersStack[0]
                val value = registersStack[0]
                val degrees = truncate(value)
                val minutes = (value - degrees) * 100
                registersStack[0] = degrees + minutes / 60
            }

            Command.DEG_TO_HM -> {
                x1 = registersStack[0]
                val value = registersStack[0]
                val degrees = truncate(value)
                val minutes = (value - degrees) * 60
                registersStack[0] = degrees + minutes / 100
            }

            Command.HMS_TO_DEG -> {
                x1 = registersStack[0]
                val value = registersStack[0]
                val degrees = truncate(value)
                val minutesSeconds = (value - degrees) * 100
                val minutes = truncate(minutesSeconds)
                val seconds = (minutesSeconds - minutes) * 100
                registersStack[0] = degrees + minutes / 60 + seconds / 3600
            }

            Command.DEG_TO_HMS -> {
                x1 = registersStack[0]
                val value = registersStack[0]
                val degrees = truncate(value)
                val totalMinutes = (value - degrees) * 60
                val minutes = truncate(totalMinutes)
                val seconds = (totalMinutes - minutes) * 60
                registersStack[0] = degrees + minutes / 100 + seconds / 10000
            }

            Command.RANDOM -> {
                x1 = registersStack[0]
                registersStack[0] = prng.nextDouble()
            }

            Command.RTN -> {
                val returnAddress = subprogramStack.removeLast()
                jumpAccumulator(returnAddress)
            }

            else -> {}
        }
        increaseAccumulator()
    }

    private fun performMemoryCommand(command: Command, term: String) {
        when(command) {
            Command.MOVT -> {
                Registers
                    .values()
                    .find { it.registerName == term }
                    ?.let { register ->
                        registersData[register.registerName] = getXRegister()
                    }
                increaseAccumulator()
            }

            Command.MOVF -> {
                Registers
                    .values()
                    .find { it.registerName == term }
                    ?.let { register ->
                        registersStack[0] = registersData[register.registerName]!!
                    }
                increaseAccumulator()
            }

            Command.IMOVT -> {
                val address = getAddress(registerName = term)
                val registerName = getRegisterByAddress(address!!)
                Registers
                    .values()
                    .find { it.registerName == registerName }
                    ?.let { register ->
                        registersData[register.registerName] = getXRegister()
                    }
                increaseAccumulator()
            }

            Command.IMOVF -> {
                val address = getAddress(registerName = term)
                val registerName = getRegisterByAddress(address!!)
                Registers
                    .values()
                    .find { it.registerName == registerName }
                    ?.let { register ->
                        registersStack[0] = registersData[register.registerName]!!
                    }
                increaseAccumulator()
            }

            else -> {}
        }
    }

    private fun performControlCommand(command: Command, term: String) {
        when (command) {
            Command.GOTO -> {
                jumpAccumulator(term.toInt())
            }

            Command.NEG -> {
                if (getXRegister() < 0) {
                    increaseAccumulator()
                } else {
                    jumpAccumulator(term.toInt())
                }
            }

            Command.NNG -> {
                if (getXRegister() >= 0) {
                    increaseAccumulator()
                } else {
                    jumpAccumulator(term.toInt())
                }
            }

            Command.ZRO -> {
                if (getXRegister() == 0.0) {
                    increaseAccumulator()
                } else {
                    jumpAccumulator(term.toInt())
                }
            }

            Command.NZR -> {
                if (getXRegister() != 0.0) {
                    increaseAccumulator()
                } else {
                    jumpAccumulator(term.toInt())
                }
            }

            Command.GOSUB -> {
                subprogramStack.addLast(acc)
                jumpAccumulator(term.toInt())
            }

            Command.LOOP0 -> {
                forLoop(
                    address = term.toInt(),
                    registerName = Registers.RG0.registerName
                )
            }

            Command.LOOP1 -> {
                forLoop(
                    address = term.toInt(),
                    registerName = Registers.RG1.registerName
                )
            }

            Command.LOOP2 -> {
                forLoop(
                    address = term.toInt(),
                    registerName = Registers.RG2.registerName
                )
            }

            Command.LOOP3 -> {
                forLoop(
                    address = term.toInt(),
                    registerName = Registers.RG3.registerName
                )
            }

            Command.IGOTO -> {
                when(term){
                    Registers.RG0.registerName,
                    Registers.RG1.registerName,
                    Registers.RG2.registerName,
                    Registers.RG3.registerName -> {
                        registersData[term]?.let { currentAddress ->
                            registersData[term] = currentAddress - 1
                        }
                    }
                    Registers.RG4.registerName,
                    Registers.RG5.registerName,
                    Registers.RG6.registerName -> {
                        registersData[term]?.let { currentAddress ->
                            registersData[term] = currentAddress + 1
                        }
                    }
                }
                val address = getAddress(registerName = term)
                if (address != null) {
                    jumpAccumulator(address)
                } else {
                    increaseAccumulator()
                }
            }

            Command.INEG -> {
                if (getXRegister() < 0) {
                    increaseAccumulator()
                } else {
                    val address = getAddress(registerName = term)
                    if (address != null) {
                        jumpAccumulator(address)
                    } else {
                        increaseAccumulator()
                    }
                }
            }

            Command.INNG -> {
                if (getXRegister() >= 0) {
                    increaseAccumulator()
                } else {
                    val address = getAddress(registerName = term)
                    if (address != null) {
                        jumpAccumulator(address)
                    } else {
                        increaseAccumulator()
                    }
                }
            }

            Command.IZRO -> {
                if (getXRegister() == 0.0) {
                    increaseAccumulator()
                } else {
                    val address = getAddress(registerName = term)
                    if (address != null) {
                        jumpAccumulator(address)
                    } else {
                        increaseAccumulator()
                    }
                }
            }

            Command.INZR -> {
                if (getXRegister() != 0.0) {
                    increaseAccumulator()
                } else {
                    val address = getAddress(registerName = term)
                    if (address != null) {
                        jumpAccumulator(address)
                    } else {
                        increaseAccumulator()
                    }
                }
            }

            Command.IGOSUB -> {
                val address = getAddress(registerName = term)
                if (address != null) {
                    subprogramStack.addLast(acc)
                    jumpAccumulator(address)
                }
            }

            else -> {}
        }
    }

    private fun increaseAccumulator() {
        acc += 1
    }

    private fun jumpAccumulator(value: Int) {
        acc = value
    }

    private fun getAddress(registerName: String): Int? {
        return Registers.values()
            .find { it.registerName == registerName }
            ?.let { registersData[it.registerName]?.toInt() }
    }

    private fun getRegisterByAddress(address: Int): String {
        return Registers
            .values()
            .getOrElse(address) {
                throw Exception ("Register not found Exception")
            }.registerName
    }

    private fun forLoop(address: Int, registerName: String) {
        getDataFromRegister(registerName)?.let { dataFromRegister ->
            val newDataFromRegister = dataFromRegister - 1
            if (newDataFromRegister != 0.0) {
                registersData[registerName] = newDataFromRegister
                jumpAccumulator(address)
            }
        }
    }

    private fun getDataFromRegister(registerName: String): Double? {
        return Registers.values()
            .find { it.registerName == registerName }
            ?.let { registersData[it.registerName] }
    }

    private fun getXRegister() = registersStack[0]
}

enum class Command(val commandMnemonics: String) {
    /**
     * No operation.
     */
    NOP("НОП"),

    /**
     * Stop operation.
     */
    STOP("С/П"),

    /**
     * Clear the current value from reg X.
     */
    CLR("СX"),

    /**
     * Push the current value onto the [X, Y, Z, T] stack.
     */
    PUSH("B↑"),

    /**
     * Shift values in the [X, Y, Z, T] stack.
     */
    SHIFT("↻"),

    /**
     * Swap X and Y values in the [X, Y, Z, T] stack.
     */
    SWAP("↔"),

    /**
     * Add the X value by the top value after X in the [X, Y, Z, T] stack.
     */
    ADD("+"),

    /**
     * Subtract X value by the top value after X in the [X, Y, Z, T] stack.
     */
    SUB("-"),

    /**
     * Multiply X value by the top value after X in the [X, Y, Z, T] stack.
     */
    MUL("×"),

    /**
     * Divide the X value by the top value after X in the [X, Y, Z, T] stack.
     */
    DIV("÷"),

    /**
     * Raise 10 to the power of the X value in the [X, Y, Z, T] stack stack.
     */
    EXP10("10^x"),

    /**
     * Raise e to the power of the X value in the [X, Y, Z, T] stack stack.
     */
    EXP("e^x"),

    /**
     * Calculate the base 10 logarithm of the X value in the [X, Y, Z, T] stack stack.
     */
    LG("lg"),

    /**
     * Calculate the natural logarithm of the X value in the [X, Y, Z, T] stack stack.
     */
    NL("ln"),

    /**
     * Calculate the sine of the X value in the [X, Y, Z, T] stack stack.
     */
    SIN("sin"),

    /**
     * Calculate the cosine of the X value in the [X, Y, Z, T] stack stack.
     */
    COS("cos"),

    /**
     * Calculate the tangent of the X value in the [X, Y, Z, T] stack stack.
     */
    TG("tg"),

    /**
     * Calculate the arcsine of the X value in the [X, Y, Z, T] stack stack.
     */
    ARCSIN("sin^-1"),

    /**
     * Calculate the arccosine of the X value in the [X, Y, Z, T] stack stack.
     */
    ARCCOS("cos^-1"),

    /**
     * Calculate the arctangent of the X value in the [X, Y, Z, T] stack stack.
     */
    ARCTG("tg^-1"),

    /**
     * Calculate the sqrt of the X value in the [X, Y, Z, T] stack stack.
     */
    SQRT("√"),

    /**
     * Calculate the 1/x of the X value in the [X, Y, Z, T] stack stack.
     */
    FRAC("1/x"),

    /**
     * Calculate the srq value of the X value in the [X, Y, Z, T] stack stack.
     */
    SQR("x^2"),

    /**
     * Calculate the x^y value of the X value in the [X, Y, Z, T] stack stack.
     */
    POW("x^y"),

    /**
     * Put π into X value in the [X, Y, Z, T] stack stack.
     */
    PI("π"),

    /**
     * Put e into X value in the [X, Y, Z, T] stack stack.
     */
    E("e"),

    /**
     * Calculate the floor of the X value in the [X, Y, Z, T] stack stack.
     */
    FLR("[x]"),

    /**
     * Calculate the fractional part of the X value in the [X, Y, Z, T] stack stack.
     */
    FRC("{x}"),

    /**
     * Calculate the absolute value of the X value in the [X, Y, Z, T] stack stack.
     */
    ABS("|x|"),

    /**
     * Calculate the maximal value between X and Y values in the [X, Y, Z, T] stack stack.
     */
    MAX("max"),

    /**
     * Define sign of the X value [X, Y, Z, T] stack stack.
     * Put -1 into X if X < 0.
     * Put 1 into X if X > 0.
     * Put 0 into X if X = 0.
     */
    SIGN("ЗН"),

    /**
     * Perform a bitwise AND between X and Y values in the [X, Y, Z, T] stack stack.
     */
    AND("∧"),

    /**
     * Perform a bitwise OR between X and Y values in the [X, Y, Z, T] stack stack.
     */
    OR("∨"),

    /**
     * Perform a bitwise XOR between X and Y values in the [X, Y, Z, T] stack stack.
     */
    XOR("⊕"),

    /**
     * Perform a bitwise NOT between X and Y values in the [X, Y, Z, T] stack stack.
     */
    NOT("¬"),

    /**
     * Move the X value from the [X, Y, Z, T] stack into one of the data register [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e].
     */
    MOVT("X→П"),

    /**
     * Move one of the data register [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] value into X register from the [X, Y, Z, T] stack
     */
    MOVF("П→X"),

    /**
     * Unconditional jump to a specified address after БП command.
     */
    GOTO("БП"),

    /**
     * Conditional jump to a specified address after x<0 command if the X value in the [X, Y, Z, T] stack is negative.
     */
    NEG("X<0"),

    /**
     * Conditional jump to a specified address after x≥0 command if the X value in the [X, Y, Z, T] stack is non-negative.
     */
    NNG("X≥0"),

    /**
     * Conditional jump to a specified address after x=0 command if the X value in the [X, Y, Z, T] stack is zero.
     */
    ZRO("X=0"),

    /**
     * Conditional jump to a specified address after x≠0 command if the X value in the [X, Y, Z, T] stack is non-zero.
     */
    NZR("X≠0"),

    /**
     * Call a subprogram/subroutine at a specified address after ПП command.
     */
    GOSUB("ПП"),

    /**
     * Return from a subprogram/subroutine.
     */
    RTN("В/O"),

    /**
     * Conditional jump to a specified address after L0 command if the GR0 value non-zero and decrease GR0 on 1
     */
    LOOP0("L0"),

    /**
     * Conditional jump to a specified address after L1 command if the GR0 value non-zero and decrease GR1 on 1
     */
    LOOP1("L1"),

    /**
     * Conditional jump to a specified address after L2 command if the GR0 value non-zero and decrease GR2 on 1
     */
    LOOP2("L2"),

    /**
     * Conditional jump to a specified address after L3 command if the GR0 value non-zero and decrease GR3 on 1
     */
    LOOP3("L3"),

    /**
     * Indirect move the X value from the [X, Y, Z, T] stack into one of the data register [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e].
     * Not implemented
     */
    IMOVT("К X→П"),

    /**
     * Indirect move one of the data register [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] value into X register from the [X, Y, Z, T] stack
     * Not implemented
     */
    IMOVF("К П→X"),

    /**
     * Indirect unconditional jump to a specified address from one of the registers [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] that goes after KБП command.
     */
    IGOTO("К БП"),

    /**
     * Indirect conditional jump to a specified address from one of the registers [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] if the X value in the [X, Y, Z, T] stack is negative.
     */
    INEG("К X<0"),

    /**
     * Indirect conditional jump to a specified address from one of the registers [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] if the X value in the [X, Y, Z, T] stack stack is non-negative.
     */
    INNG("К X≥0"),

    /**
     * Indirect conditional jump to a specified address from one of the registers [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] if the X value in the [X, Y, Z, T] stack stack is zero.
     */
    IZRO("К X=0"),

    /**
     * Indirect conditional jump to a specified address from one of the registers [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e] if the X value in the [X, Y, Z, T] stack stack is non-zero.
     */
    INZR("К X≠0"),

    /**
     * Indirect call a subprogram/subroutine at a specified address from one of the registers [0, 1, 2, 4, 5, 6, 7, 8. 9, a, b, c, d, e].
     */
    IGOSUB("К ПП"),

    /**
     * Restore the previous result: lift the stack and recall the X1 (last-x) register into X.
     */
    LASTX("Вx"),

    /**
     * Convert an angle in degrees (hours), minutes and fractions of a minute (DD.MMmmm) to
     * degrees (hours) and fractions of a degree (DD.dddd).
     */
    HM_TO_DEG("°←'"),

    /**
     * Convert an angle in degrees (hours) and fractions of a degree (DD.dddd) to degrees (hours),
     * minutes and fractions of a minute (DD.MMmmm).
     */
    DEG_TO_HM("°→'"),

    /**
     * Convert an angle in degrees (hours), minutes, seconds and fractions of a second (DD.MMSSsss)
     * to degrees (hours) and fractions of a degree (DD.dddd).
     */
    HMS_TO_DEG("°←''\""),

    /**
     * Convert an angle in degrees (hours) and fractions of a degree (DD.dddd) to degrees (hours),
     * minutes, seconds and fractions of a second (DD.MMSSsss).
     */
    DEG_TO_HMS("°→''\""),

    /**
     * Generate a pseudo-random number in the range [0, 1) and place it into the X register.
     */
    RANDOM("СЧ"),
}

enum class Registers(val registerName: String) {
    RG0("0"),
    RG1("1"),
    RG2("2"),
    RG3("3"),
    RG4("4"),
    RG5("5"),
    RG6("6"),
    RG7("7"),
    RG8("8"),
    RG9("9"),
    RGA("a"),
    RGB("b"),
    RGC("c"),
    RGD("d"),
    RGE("e"),
}

/**
 * Extension function to convert an angle measured in degrees to radians.
 *
 * @return The angle in radians.
 */
fun Double.toRadians(): Double = this * KOTLIN_PI / 180.0

/**
 * Extension function to convert an angle measured in radians to degrees.
 *
 * @return The angle in degrees.
 */
fun Double.toDegrees(): Double = this * 180.0 / KOTLIN_PI

/**
 * Extension function to check if a string is a number.
 *
 * @return True if the string can be converted to a Double, false otherwise.
 */
fun String.isNumber(): Boolean {
    return this.toDoubleOrNull() != null
}

/**
 * Extension function to push the elements of a mutable list of doubles.
 * Shifts all elements to the right, duplicating the first element.
 */
fun MutableList<Double>.push() {
    for (i in this.size - 1 downTo 1) {
        this[i] = this[i - 1]
    }
}

/**
 * Extension function to shift the elements of a mutable list of doubles.
 * Shifts all elements to the left, moving the first element to the end.
 */
fun MutableList<Double>.shift() {
    val first = this[0]
    for (i in 0 until this.size - 1) {
        this[i] = this[i + 1]
    }
    this[this.size - 1] = first
}

/**
 * Extension function to swap the first two elements of a mutable list of doubles.
 */
fun MutableList<Double>.swap() {
    val first = this[0]
    this[0] = this[1]
    this[1] = first
}

/**
 * Extension function to shift the elements of a mutable list of doubles to the left.
 * Moves the second element to the first position, the third to the second, and so on.
 */
fun MutableList<Double>.shiftLeft() {
    this[1] = this[2]
    this[2] = this[3]
}

/**
 * Extension function to check if a string contains a memory command.
 *
 * @return True if the string contains a memory command, false otherwise.
 */
fun String.hasMemoryCommand(): Boolean {
    return this.contains("X→П")
            || this.contains("П→X")
            || this.contains("К X→П")
            || this.contains("К П→X")
}

/**
 * Extension function to check if a command is a control command.
 *
 * @return True if the command is a control command, false otherwise.
 */
fun Command.isControlCommand(): Boolean {
    return this.commandMnemonics == "БП"
            || this.commandMnemonics == "X<0"
            || this.commandMnemonics == "X≥0"
            || this.commandMnemonics == "X=0"
            || this.commandMnemonics == "X≠0"
            || this.commandMnemonics == "ПП"
            || this.commandMnemonics == "L0"
            || this.commandMnemonics == "L1"
            || this.commandMnemonics == "L2"
            || this.commandMnemonics == "L3"
            || this.commandMnemonics == "К БП"
            || this.commandMnemonics == "К X<0"
            || this.commandMnemonics == "К X≥0"
            || this.commandMnemonics == "К X=0"
            || this.commandMnemonics == "К X≠0"
            || this.commandMnemonics == "К ПП"
}