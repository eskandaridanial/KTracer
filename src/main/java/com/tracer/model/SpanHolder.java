package com.tracer.model;

import io.opentelemetry.api.trace.Span;

/**
 * @author doneskandari@gmail.com
 * Date    2023-09-24 19:39:09
 */

/**
 * The {@code SpanHolder} class is a utility class that provides a thread-local storage mechanism
 * for managing OpenTelemetry Spans within the current thread's context. It allows for the retrieval,
 * setting, and removal of Spans associated with the current thread.
 */
public class SpanHolder {
    private static final ThreadLocal<Span> threadLocalSpan = ThreadLocal.withInitial(() -> null);
    public static Span getSpan() {
        return threadLocalSpan.get();
    }
    public static void setSpan(Span span) {
        threadLocalSpan.set(span);
    }
    public static void removeSpan() {
        threadLocalSpan.remove();
    }
}
