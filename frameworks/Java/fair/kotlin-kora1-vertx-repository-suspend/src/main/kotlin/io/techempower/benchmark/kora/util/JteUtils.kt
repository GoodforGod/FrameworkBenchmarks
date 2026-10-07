package io.techempower.benchmark.kora.util

import gg.jte.generated.precompiled.JtefortunesGenerated
import gg.jte.html.OwaspHtmlTemplateOutput
import gg.jte.output.Utf8ByteOutput
import io.techempower.benchmark.kora.model.Fortune

object JteUtils {
    fun serializeStandard(fortunes: List<Fortune>): ByteArray {
        val out = Utf8ByteOutput(256)
        JtefortunesGenerated.render(OwaspHtmlTemplateOutput(out), null, fortunes)
        return out.toByteArray()
    }
}
