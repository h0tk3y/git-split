package com.github.h0tk3y.gitsplit

import java.io.File

internal fun executeSplitInstruction(instruction: SplitInstruction) {
    val branchesNeeded = instruction.splitFilesFromTo.values.map { it.size }.max() ?: 0
    val stepsNeeded = branchesNeeded + 2

    data class GitMoveCommand(val from: File, val to: File)

    val orderedInstruction = instruction.splitFilesFromTo.mapValues { it.value.toList() }

    val movesByBranch = (0 until branchesNeeded).map { i ->
        orderedInstruction.mapNotNull { (fromFile, toFiles) ->
            if (i !in toFiles.indices) null else
                GitMoveCommand(fromFile, toFiles[i])
        }
    }

    val copyCommits = mutableListOf<String>()

    val description =
        "Copy files with Git history (automatic commit)\n\n" +
                instruction.splitFilesFromTo.entries.joinToString("\n") { (k, v) ->
                    "  - from $k\n" + v.joinToString("\n") { "    - to $it" }
                }

    movesByBranch.forEachIndexed { index, branchCommands ->
        branchCommands.forEach { command -> gitMove(command.from, command.to) }

        val commitMessage = "(step ${index + 1}/$stepsNeeded) " +
                description +
                "\n\nThis commit moves:\n\n" +
                branchCommands.joinToString("\n") { "  " + it.from.path + " -> " + it.to.path }
        gitCommit(commitMessage)

        copyCommits.add(gitRevParseHead())
        gitResetToPreviousCommit()
    }

    val tmpFileSuffix = ".git_split_tmp"
    orderedInstruction.keys.forEach {
        gitMove(it, File(it.path + tmpFileSuffix))
    }

    gitCommit(
        "(step ${stepsNeeded - 1}/$stepsNeeded) " + description +
                "\n\nThis commit moves the original files to \n" +
                "temporary files suffixed with '$tmpFileSuffix'"
    )

    gitMerge(copyCommits)

    orderedInstruction.keys.forEach { gitMove(File(it.path + tmpFileSuffix), it) }
    gitCommit(
        "(step $stepsNeeded/$stepsNeeded) " +
                description +
                "\n\nThis commit moves the temporary files back to their original paths."
    )
}

private fun gitMove(from: File, to: File) {
    exec("git", "mv", from.path, to.path)
}

private fun gitCommit(commitMessage: String, add: Boolean = false) {
    exec(
        "git", "commit",
        *(if (add) arrayOf("-a") else arrayOf()),
        "-n",
        "-m", commitMessage
    )
}

private fun gitResetToPreviousCommit() {
    exec("git", "reset", "--hard", "HEAD^")
}

private fun gitRevParseHead(): String =
    exec("git", "rev-parse", "HEAD").removeSuffix("\n")

private fun gitMerge(revisions: List<String>) {
    exec(
        "git", "merge",
        *(if (revisions.size == 1) arrayOf("-s", "resolve") else arrayOf()),
        *revisions.toTypedArray()
    )
}