package io.gitlab.arturbosch.detekt

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.invoke.*
import io.gitlab.arturbosch.detekt.invoke.AutoCorrectArgument
import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.BuildUponDefaultConfigArgument
import io.gitlab.arturbosch.detekt.invoke.ClasspathArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.CustomReportArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DefaultReportArgument
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.FailFastArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.JvmTargetArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import io.gitlab.arturbosch.detekt.output.mergeXmlReports
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

open class DetektDiff : Detekt() {

//    val filePrefix = project.projectDir.name
//
//    private fun getDiffFiles(): String {
//        println("Start")
//        val outputStream = ByteArrayOutputStream()
//        val execResult = project.exec {
//            //it.commandLine("git diff HEAD --name-only")
//            it.commandLine("git", "diff", "HEAD", "--name-only")
//            it.standardOutput = outputStream
//        }
//        if (execResult.exitValue == 0) {
//            val files = parseDiffFiles(outputStream.toString()).toString()
//            setSource(files)
//        } else {
//            println("Diff failed")
//        }
//        return "asd"
//    }
//
//    private fun parseDiffFiles(streamOutput: String): List<File> =
//        streamOutput.split("\n")
//            .map { it.removePrefix("$filePrefix/") }
//            .map { filePath -> File(filePath) }
//
//    @TaskAction
//    override fun check() {
//        val diffFiles = getDiffFiles()
//        println("COMPLETE")
//        if (plugins.isPresent && !pluginClasspath.isEmpty)
//            throw GradleException(
//                "Cannot set value for plugins on detekt task and apply detektPlugins configuration " +
//                        "at the same time."
//            )
//        val xmlReportTargetFileOrNull = xmlReportFile.orNull
//        val htmlReportTargetFileOrNull = htmlReportFile.orNull
//        val txtReportTargetFileOrNull = txtReportFile.orNull
//        val debugOrDefault = debugProp.getOrElse(false)
//        val arguments = mutableListOf(
//            InputArgument(source),
//            ClasspathArgument(classpath),
//            JvmTargetArgument(jvmTargetProp.orNull),
//            ConfigArgument(config),
//            PluginsArgument(plugins.orNull),
//            BaselineArgument(baseline.orNull),
//            DefaultReportArgument(DetektReportType.XML, xmlReportTargetFileOrNull),
//            DefaultReportArgument(DetektReportType.HTML, htmlReportTargetFileOrNull),
//            DefaultReportArgument(DetektReportType.TXT, txtReportTargetFileOrNull),
//            DebugArgument(debugOrDefault),
//            ParallelArgument(parallelProp.getOrElse(false)),
//            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
//            FailFastArgument(failFastProp.getOrElse(false)),
//            AutoCorrectArgument(autoCorrectProp.getOrElse(false)),
//            DisableDefaultRuleSetArgument(disableDefaultRuleSetsProp.getOrElse(false))
//        )
//        arguments.addAll(customReports.get().map {
//            val reportId = it.reportIdProp.orNull
//            val destination = it.destinationProperty.orNull
//
//            checkNotNull(reportId) { "If a custom report is specified, the reportId must be present" }
//            check(!DetektReportType.isWellKnownReportId(reportId)) {
//                "The custom report reportId may not be same as one of the default reports"
//            }
//            checkNotNull(destination) { "If a custom report is specified, the destination must be present" }
//
//            CustomReportArgument(reportId, destination)
//        })
//
//        DetektInvoker.invokeCli(
//            project = project,
//            arguments = arguments.toList(),
//            ignoreFailures = ignoreFailuresProp.getOrElse(false),
//            classpath = detektClasspath.plus(pluginClasspath),
//            taskName = name
//        )
//
//        if (name == DetektPlugin.DETEKT_TASK_NAME && xmlReportTargetFileOrNull != null) {
//            val xmlReports = project.subprojects.flatMap { subproject ->
//                subproject.tasks.mapNotNull { task ->
//                    if (task is Detekt && task.name == DetektPlugin.DETEKT_TASK_NAME)
//                        task.xmlReportFile.orNull?.asFile
//                    else
//                        null
//                }
//            }
//            if (!xmlReports.isEmpty() && debugOrDefault) {
//                logger.info("Merging report files of subprojects $xmlReports into $xmlReportTargetFileOrNull")
//            }
//            mergeXmlReports(xmlReportTargetFileOrNull.asFile, xmlReports)
//        }
//    }

}
