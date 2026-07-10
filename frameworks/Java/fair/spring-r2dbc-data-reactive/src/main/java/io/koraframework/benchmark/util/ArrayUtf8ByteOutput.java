package io.koraframework.benchmark.util;

import gg.jte.TemplateOutput;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ArrayUtf8ByteOutput implements TemplateOutput {

    private byte[] buf;
    private int count;

    public ArrayUtf8ByteOutput() {
        this(1024);
    }

    public ArrayUtf8ByteOutput(int initialSize) {
        this.buf = new byte[initialSize];
    }

    public ByteBuffer buffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }

    public int length() {
        return count;
    }

    private void ensureCapacity(int additional) {
        int needed = count + additional;
        if (needed > buf.length) {
            int newCap;
            do {
                newCap = buf.length + 1024;
            } while (newCap < needed);

            buf = Arrays.copyOf(buf, newCap);
        }
    }

    private void write(byte[] bytes, int off, int len) {
        ensureCapacity(len);
        System.arraycopy(bytes, off, buf, count, len);
        count += len;
    }

    @Override
    public void writeContent(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeContent(String s, int beginIndex, int endIndex) {
        byte[] bytes = s.substring(beginIndex, endIndex).getBytes(StandardCharsets.UTF_8);
        write(bytes, 0, bytes.length);
    }

    @Override
    public void writeBinaryContent(byte[] value) {
        write(value, 0, value.length);
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(buf, length()));
    }
}
