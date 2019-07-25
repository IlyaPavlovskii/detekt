package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import io.gitlab.arturbosch.detekt.cli.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

private const val EXPECTED_CONTENT_RES_FILE = "short-issues-report.txt"

class ShortIssuesReportSpec : Spek({

    val subject by memoized { ShortIssuesReport() }

    describe("short issues report") {

        context("several detekt findings") {

            it("reports the debt per ruleset and the overall debt") {
                val expectedContent = readResource(EXPECTED_CONTENT_RES_FILE)
                val detektion = object : TestDetektion() {
                    override val findings: Map<String, List<Finding>> = mapOf(
                        Pair("TestSmell", listOf(createFinding(), createFinding())),
                        Pair("EmptySmells", emptyList())
                    )
                }
                val output = subject.render(detektion)?.trimEnd()?.decolorized()
                assertThat(output).isEqualTo(expectedContent)
            }

            it("reports no findings") {
                val detektion = TestDetektion()
                val report = subject.render(detektion)
                assertThat(report).isEmpty()
            }
        }
    }

})
