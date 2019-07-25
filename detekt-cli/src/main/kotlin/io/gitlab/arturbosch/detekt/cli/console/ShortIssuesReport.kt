package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class ShortIssuesReport : ConsoleReport() {

    override val priority: Int = 60

    override fun render(detektion: Detektion): String? {
        val findings = detektion.findings
        return with(StringBuilder()) {
            findings.values.forEach { findings ->
                findings.forEach { finding ->
                    append(finding.compact()).append("\n")
                }
            }
            if (isNotEmpty()) {
                toString().removeSuffix("\n").yellow()
            } else {
                ""
            }
        }
    }
}
