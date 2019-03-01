package com.github.h0tk3y.gitsplit

import java.io.File

internal class SplitInstruction(
    val splitFilesFromTo: Map<File, Set<File>>
)

internal fun parseSplitInstruction(args: List<String>): SplitInstruction {
    val splitFilesFromTo = mutableMapOf<File, Set<File>>()

    var state: String? = null
    var fromFile: File? = null
    var toFiles = mutableSetOf<File>()

    for (arg in args) {
        when (state) {
            null -> when (arg) {
                FROM_MARKER -> {
                    state = FROM_MARKER
                    fromFile = null
                    toFiles = mutableSetOf()
                }
                else -> error("expected: $FROM_MARKER, got '$arg'")
            }
            FROM_MARKER -> when (arg) {
                FROM_MARKER -> error("expected file name, got '$arg'")
                TO_MARKER ->
                    if (fromFile == null) error("expected file name, got '$arg'") else state = TO_MARKER
                else -> fromFile = File(arg)
            }
            TO_MARKER -> when (arg) {
                FROM_MARKER -> {
                    if (toFiles.isEmpty()) {
                        error("expected non-empty file names list after $TO_MARKER, got '$arg'")
                    }
                    splitFilesFromTo.compute(fromFile!!) { _, old -> old.orEmpty() + toFiles }

                    state = FROM_MARKER
                    fromFile = null
                    toFiles = mutableSetOf()
                }
                TO_MARKER -> Unit
                else -> toFiles.add(File(arg))
            }
        }
    }

    if (state == FROM_MARKER) {
        error("expected '$TO_MARKER' and then non-empty file names list")
    }

    if (state == TO_MARKER) {
        if (toFiles.isEmpty()) {
            error("expected non-empty file names list after last '$TO_MARKER'")
        } else {
            splitFilesFromTo.compute(fromFile!!) { _, old -> old.orEmpty() + toFiles }
        }
    }

    return SplitInstruction(splitFilesFromTo)
}

internal const val FROM_MARKER = "--from"
internal const val TO_MARKER = "--to"