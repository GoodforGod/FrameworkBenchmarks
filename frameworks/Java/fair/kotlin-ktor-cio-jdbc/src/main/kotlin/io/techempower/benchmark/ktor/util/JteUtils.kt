package io.techempower.benchmark.ktor.util

import gg.jte.generated.precompiled.JtefortunesGenerated
import gg.jte.html.OwaspHtmlTemplateOutput
import gg.jte.output.Utf8ByteOutput
import io.techempower.benchmark.ktor.model.Fortune

object JteUtils {
    fun serializeStandard(fortunes: List<Fortune>): ByteArray {
        val output = Utf8ByteOutput(256)
        JtefortunesGenerated.render(OwaspHtmlTemplateOutput(output), null, fortunes)
        return output.toByteArray()
    }
}
