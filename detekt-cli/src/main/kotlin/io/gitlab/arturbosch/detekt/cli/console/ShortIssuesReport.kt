package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class ShortIssuesReport : ConsoleReport() {

    override val priority: Int = 60

    override fun render(detektion: Detektion): String? {
        val findings = detektion.findings
        return with(StringBuilder()) {
            findings.values.forEach { findings ->
                findings.forEachIndexed { index, finding ->
                    append(finding.compact())
                    if (index < findings.size - 1) {
                        append("\n")
                    }
                }
            }
            if (isNotEmpty()) {
                toString().yellow()
            } else {
                ""
            }
        }
    }
}
