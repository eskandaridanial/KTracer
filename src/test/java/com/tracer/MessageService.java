package com.tracer;

import com.tracer.model.SpanHolder;
import io.opentelemetry.api.trace.Span;
import org.slf4j.MDC;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * @author doneskandari@gmail.com
 * Date    2023-10-13 00:47:53
 */
public class MessageService {

    @ServiceActivator
    public void testActivator() {
        Span span = SpanHolder.getSpan();
        MDC.put("traceId", span.getSpanContext().getTraceId());
        MDC.put("spanId", span.getSpanContext().getSpanId());
    }
}
