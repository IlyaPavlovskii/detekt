package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DetektPlugin.Companion.DETEKT_TASK_NAME
import io.gitlab.arturbosch.detekt.extensions.CustomDetektReport
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.internal.fileProperty
import io.gitlab.arturbosch.detekt.invoke.*
import io.gitlab.arturbosch.detekt.output.mergeXmlReports
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.file.*
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.*
import org.gradle.api.tasks.Optional
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@CacheableTask
open class Detekt : SourceTask(), VerificationTask {

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        get() = source
        set(value) = setSource(value)

    @Input
    @Optional
    @Deprecated("Replace with setIncludes/setExcludes")
    var filters: Property<String> = project.objects.property(String::class.java)

    @Classpath
    val detektClasspath = project.configurableFileCollection()

    @Classpath
    val pluginClasspath = project.configurableFileCollection()

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var baseline: RegularFileProperty = project.fileProperty()

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var config: ConfigurableFileCollection = project.configurableFileCollection()

    @Classpath
    @Optional
    val classpath = project.configurableFileCollection()

    @Input
    @Optional
    internal val jvmTargetProp: Property<String> = project.objects.property(String::class.javaObjectType)
    var jvmTarget: String
        @Internal
        get() = jvmTargetProp.get()
        set(value) = jvmTargetProp.set(value)

    @Input
    @Optional
    @Deprecated(
        "Set plugins using the detektPlugins configuration " +
                "(see https://arturbosch.github.io/detekt/extensions.html#let-detekt-know-about-your-extensions)"
    )
    var plugins: Property<String> = project.objects.property(String::class.java)

    @Internal
    @Optional
    val debugProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var debug: Boolean
        @Internal
        get() = debugProp.get()
        set(value) = debugProp.set(value)

    @Internal
    @Optional
    val parallelProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var parallel: Boolean
        @Internal
        get() = parallelProp.get()
        set(value) = parallelProp.set(value)

    @Optional
    @Input
    val disableDefaultRuleSetsProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var disableDefaultRuleSets: Boolean
        @Internal
        get() = disableDefaultRuleSetsProp.get()
        set(value) = disableDefaultRuleSetsProp.set(value)

    @Optional
    @Input
    val buildUponDefaultConfigProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var buildUponDefaultConfig: Boolean
        @Internal
        get() = buildUponDefaultConfigProp.get()
        set(value) = buildUponDefaultConfigProp.set(value)

    @Optional
    @Input
    val failFastProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var failFast: Boolean
        @Internal
        get() = failFastProp.get()
        set(value) = failFastProp.set(value)

    val ignoreFailuresProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    @Input
    @Optional
    override fun getIgnoreFailures(): Boolean = ignoreFailuresProp.get()

    override fun setIgnoreFailures(value: Boolean) = ignoreFailuresProp.set(value)
    fun setIgnoreFailures(value: Provider<Boolean>) = ignoreFailuresProp.set(value)

    @Optional
    @Input
    val autoCorrectProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var autoCorrect: Boolean
        @Internal
        get() = autoCorrectProp.get()
        set(value) = autoCorrectProp.set(value)

    @Internal
    var reports = DetektReports(project)

    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    @Internal
    @Optional
    var reportsDir: Property<File> = project.objects.property(File::class.java)

    val xmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.xml.getTargetFileProvider(effectiveReportsDir)

    val htmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.html.getTargetFileProvider(effectiveReportsDir)

    val txtReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.txt.getTargetFileProvider(effectiveReportsDir)

    private val defaultReportsDir: Directory = project.layout.buildDirectory.get()
        .dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
        .dir("detekt")

    private val effectiveReportsDir = project.provider { reportsDir.getOrElse(defaultReportsDir.asFile) }

    val customReports: Provider<Collection<CustomDetektReport>>
        @Nested
        get() = project.provider { reports.custom }

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }


    val filePrefix = project.projectDir.name

    private fun getDiffFiles(): String {
        println("Start")
        val outputStream = ByteArrayOutputStream()
        val execResult = project.exec {
            it.commandLine("git", "diff", "HEAD", "--name-only")
            it.standardOutput = outputStream
        }
        if (execResult.exitValue == 0) {
            val files = parseDiffFiles(outputStream.toString())
//            println("Files: ${files.size}")
//            println(files)
//            val cfc = project.configurableFileCollection()
//            cfc.from()
//            cfc.setFrom(files)
            setSource(files)
        } else {
            //TODO Throw exception
            println("Diff failed")
        }
        return "asd"
    }

    private fun parseDiffFiles(streamOutput: String) =
        streamOutput.split("\n")
            .filter { it.startsWith(filePrefix) }
            .map { it.removePrefix("$filePrefix/") }
            .filter { it.isNotEmpty() }

    @TaskAction
    open fun check() {
        getDiffFiles()
        if (plugins.isPresent && !pluginClasspath.isEmpty)
            throw GradleException(
                "Cannot set value for plugins on detekt task and apply detektPlugins configuration " +
                        "at the same time."
            )
        val xmlReportTargetFileOrNull = xmlReportFile.orNull
        val htmlReportTargetFileOrNull = htmlReportFile.orNull
        val txtReportTargetFileOrNull = txtReportFile.orNull
        val debugOrDefault = debugProp.getOrElse(false)
        val arguments = mutableListOf(
            InputArgument(source),
            ClasspathArgument(classpath),
            JvmTargetArgument(jvmTargetProp.orNull),
            ConfigArgument(config),
            PluginsArgument(plugins.orNull),
            BaselineArgument(baseline.orNull),
            DefaultReportArgument(DetektReportType.XML, xmlReportTargetFileOrNull),
            DefaultReportArgument(DetektReportType.HTML, htmlReportTargetFileOrNull),
            DefaultReportArgument(DetektReportType.TXT, txtReportTargetFileOrNull),
            DebugArgument(debugOrDefault),
            ParallelArgument(parallelProp.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
            FailFastArgument(failFastProp.getOrElse(false)),
            AutoCorrectArgument(autoCorrectProp.getOrElse(false)),
            DisableDefaultRuleSetArgument(disableDefaultRuleSetsProp.getOrElse(false))
        )
        arguments.addAll(customReports.get().map {
            val reportId = it.reportIdProp.orNull
            val destination = it.destinationProperty.orNull

            checkNotNull(reportId) { "If a custom report is specified, the reportId must be present" }
            check(!DetektReportType.isWellKnownReportId(reportId)) {
                "The custom report reportId may not be same as one of the default reports"
            }
            checkNotNull(destination) { "If a custom report is specified, the destination must be present" }

            CustomReportArgument(reportId, destination)
        })

        DetektInvoker.invokeCli(
            project = project,
            arguments = arguments.toList(),
            ignoreFailures = ignoreFailuresProp.getOrElse(false),
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name
        )

        if (name == DETEKT_TASK_NAME && xmlReportTargetFileOrNull != null) {
            val xmlReports = project.subprojects.flatMap { subproject ->
                subproject.tasks.mapNotNull { task ->
                    if (task is Detekt && task.name == DETEKT_TASK_NAME)
                        task.xmlReportFile.orNull?.asFile
                    else
                        null
                }
            }
            if (!xmlReports.isEmpty() && debugOrDefault) {
                logger.info("Merging report files of subprojects $xmlReports into $xmlReportTargetFileOrNull")
            }
            mergeXmlReports(xmlReportTargetFileOrNull.asFile, xmlReports)
        }
    }
}
