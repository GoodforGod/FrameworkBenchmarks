package io.techempower.benchmark.micronaut.util;

import gg.jte.generated.precompiled.JtefortunesGenerated;
import gg.jte.html.HtmlTemplateOutput;
import gg.jte.html.OwaspHtmlTemplateOutput;
import gg.jte.output.Utf8ByteOutput;
import io.techempower.benchmark.micronaut.model.Fortune;

import java.util.List;

public final class JteUtils {

    private JteUtils() {
    }

    public static byte[] serializeStandard(List<Fortune> fortunes) {
        var out = new Utf8ByteOutput(256);
        HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
        JtefortunesGenerated.render(template, null, fortunes);
        return out.toByteArray();
    }
}
