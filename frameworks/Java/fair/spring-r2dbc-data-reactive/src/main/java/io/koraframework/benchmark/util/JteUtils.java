package io.koraframework.benchmark.util;

import gg.jte.generated.precompiled.JtefortunesGenerated;
import gg.jte.html.HtmlTemplateOutput;
import gg.jte.html.OwaspHtmlTemplateOutput;
import gg.jte.output.Utf8ByteOutput;
import io.koraframework.benchmark.model.Fortune;

import java.nio.ByteBuffer;
import java.util.List;

public final class JteUtils {

    private JteUtils() { }

    public static byte[] serializeStandard(List<Fortune> fortunes) {
        var out = new Utf8ByteOutput(64);
        HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
        JtefortunesGenerated.render(template, null, fortunes);
        return out.toByteArray();
    }

    public static ByteBuffer serializeCustom(List<Fortune> fortunes) {
        var out = new ArrayUtf8ByteOutput(256);
        HtmlTemplateOutput template = new OwaspHtmlTemplateOutput(out);
        JtefortunesGenerated.render(template, null, fortunes);
        return out.buffer();
    }
}
