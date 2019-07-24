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
//                    append(finding.issue.severity)
//                        .append(":")
//                        .append(finding.id)
//                        .append(" - ")
//                        .append(finding.location.compact())
//                        .append("\n")
                    append(finding.compact()).append("\n")
                }
            }
            toString().yellow()
        }
    }
}
