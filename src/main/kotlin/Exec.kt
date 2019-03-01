package com.github.h0tk3y.gitsplit

import java.io.File

internal fun exec(vararg commandLine: String) = exec(commandLine.asList())

internal fun exec(commandLine: List<String>): String {
    val command = commandLine.joinToString(" ") + "\n"
    println(command)
    val process = ProcessBuilder(commandLine).directory(File(".")).start()
    val returnValue = process.inputStream.readBytes().toString(Charsets.UTF_8)
    val err = process.errorStream.readBytes().toString(Charsets.UTF_8)
    val intResult = process.waitFor()
    if (intResult != 0) {
        val message = buildString {
            appendln("Command finished with non-zero code $intResult")
            appendln("Output: " + returnValue)
            appendln("Error stream: " + err)
        }
        error(message)
    }
    return returnValue
}


