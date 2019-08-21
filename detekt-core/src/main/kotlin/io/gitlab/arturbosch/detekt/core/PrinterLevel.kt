package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.core.PrinterFlag.DEBUG
import io.gitlab.arturbosch.detekt.core.PrinterFlag.ERROR
import io.gitlab.arturbosch.detekt.core.PrinterFlag.INFO

/**
 * Possible printer message flags.
 * */
object PrinterFlag {
    const val DEBUG = 0b0001
    const val INFO = 0b0010
    const val ERROR = 0b0100
}

/**
 * Output stream possible printer level.
 * */
object PrinterLevel {
    val DEBUG_LEVEL = toPrinterLevel(DEBUG, INFO, ERROR)
    val INFO_LEVEL = toPrinterLevel(INFO, ERROR)
    val ERROR_LEVEL = toPrinterLevel(ERROR)
}

/**
 * Check value has bit flag.
 * */
fun Int.hasFlag(flag: Int) = this and flag == flag

/**
 * Execute block when level has bit flag.
 * */
fun Int.runBlockWhenHasFlag(flag: Int, block: () -> Unit) {
    if (this.hasFlag(flag)) {
        block()
    }
}

/**
 * Convert printer flags to printer level value.
 * */
fun toPrinterLevel(vararg flags: Int): Int = flags.reduce { acc, i -> acc or i }
