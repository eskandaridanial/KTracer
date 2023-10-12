package com.tracer.interceptors;

import com.tracer.model.SpanHolder;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author doneskandari@gmail.com
 * Date    2023-09-22 19:53:34
 */

@Component
public class TracingInterceptor implements ChannelInterceptor {
    private final Tracer tracer;

    public TracingInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * This method is an implementation of a preSend() method for a message interceptor.
     * It processes incoming messages and extracts relevant information from the message headers.
     * If trace information (traceId and spanId) is present in the headers, it creates a SpanContext
     * and starts a new Span using the OpenTelemetry tracer. The Span is associated with the extracted
     * context. Otherwise, it starts a new Span without a parent context.
     *
     * @param message The incoming message to be processed.
     * @param channel The message channel through which the message is sent.
     * @return unmodified message.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();

        String httpRequestUrl = String.valueOf(headers.get("http_requestUrl"));
        String httpRequestMethod = String.valueOf(headers.get("http_requestMethod"));

        Optional<String> traceId = Optional.ofNullable((String) headers.get("x-trace-id"));
        Optional<String> spanId = Optional.ofNullable((String) headers.get("x-span-id"));
        Optional<String> traceFlags = Optional.ofNullable((String) headers.get("x-trace-flags"));
        Optional<SpanContext> spanContext = traceId.isPresent() && spanId.isPresent() && traceFlags.isPresent()
                ? Optional.of(SpanContext.createFromRemoteParent(
                        traceId.get(),
                        spanId.get(),
                        TraceFlags.fromHex(traceFlags.get(), 0),
                        TraceState.getDefault()))
                : Optional.empty();

        Span span = SpanHolder.getSpan();
        if (spanContext.isPresent()) {
            span = this.tracer
                    .spanBuilder(httpRequestUrl)
                    .setParent(Context.current()
                            .with(Span.wrap(spanContext.get())))
                    .setAttribute("http.url", httpRequestUrl)
                    .setAttribute("http.method", httpRequestMethod)
                    .startSpan();
        } else if (span != null) {
            span = this.tracer
                    .spanBuilder(httpRequestUrl)
                    .setParent(Context.current()
                            .with(Span.wrap(span.getSpanContext())))
                    .setAttribute("http.url", httpRequestUrl)
                    .setAttribute("http.method", httpRequestMethod)
                    .startSpan();
        } else {
            span = this.tracer
                    .spanBuilder(httpRequestUrl)
                    .setAttribute("http.url", httpRequestUrl)
                    .setAttribute("http.method", httpRequestMethod)
                    .startSpan();
        }
        SpanHolder.setSpan(span);

        return ChannelInterceptor.super.preSend(message, channel);
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        SpanHolder.getSpan().end();
        ChannelInterceptor.super.postSend(message, channel, sent);
    }
}