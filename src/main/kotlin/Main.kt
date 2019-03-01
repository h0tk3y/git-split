package com.github.h0tk3y.gitsplit

import java.io.File

fun main(args: Array<String>) {
    val instruction = if (args.getOrNull(0) == "--instruction") {
        val path = args.getOrNull(1) ?: error("expected instruction file name after --file")
        parseSplitInstruction(File(path).readLines().filter { it.isNotBlank() }.map { it.trim() })
    } else {
        if (args.isEmpty()) {
            println("""
                Copy files preserving Git history for all of the resulting files.

                Usage:
                git-split {--from filename {--to filename}+ }+
                git-split --instruction filename

                The --instruction file format is the same as command line format, with arguments
                put on separate lines, for example:

                --from
                  abc.txt
                --to
                  abc1.txt
                  abc2.txt

                --from
                  def.txt
                --to
                  def123.txt
            """.trimIndent())
            return
        }
        parseSplitInstruction(args.toList())
    }

    executeSplitInstruction(instruction)
}

