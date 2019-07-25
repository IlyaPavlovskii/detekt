package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

object VcsDiffUtil {

    private const val GIT_DIFF_COMMAND = "git diff HEAD --name-only"

    fun getChangedFiles(
        project: Project,
        extension: DetektExtension
    ): List<String> {
        val filePrefix = extension.diffFilePrefix ?: project.projectDir.name + "/"
        return extension.cliDiffCommand?.let {
            getChangedFiles(project, it, filePrefix)
        } ?: getChangedFiles(project, filePrefix = filePrefix)
    }

    private fun getChangedFiles(
        project: Project,
        diffCliCommand: String = GIT_DIFF_COMMAND,
        filePrefix: String
    ): List<String> {
        val outputStream = ByteArrayOutputStream()
        val execResult = project.exec {
            it.commandLine(diffCliCommand.split(" "))
            it.standardOutput = outputStream
        }
        return if (execResult.exitValue == 0) {
            parseDiffFiles(outputStream.toString(), filePrefix)
        } else {
            throw RuntimeException("CLI command execution error. Error code ${execResult.exitValue}")
        }
    }

    private fun parseDiffFiles(
        streamOutput: String,
        filePrefix: String
    ) = streamOutput.split("\n")
        .filter { it.startsWith(filePrefix) }
        .map { it.removePrefix(filePrefix) }
        .filter { it.isNotEmpty() }

}
