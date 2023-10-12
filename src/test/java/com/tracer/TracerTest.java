package com.tracer;

import com.tracer.model.SpanHolder;
import io.opentelemetry.api.trace.SpanContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author doneskandari@gmail.com
 * Date    2023-10-12 23:10:01
 */
@SpringJUnitConfig(IntegrationTestConfig.class)
public class TracerTest {

    @Autowired
    private MessageChannel inputChannel;

    @Test
    public void success() {
        String message = "success";
        Message<String> testMessage = MessageBuilder.withPayload(message).build();
        inputChannel.send(testMessage);
        SpanContext span = SpanHolder.getSpan().getSpanContext();
        Assertions.assertEquals(MDC.get("traceId"), span.getTraceId());
        Assertions.assertEquals(MDC.get("spanId"), span.getSpanId());
        MDC.clear();
    }

    @Test
    public void failure() {
        String message = "failure";
        Message<String> testMessage = MessageBuilder.withPayload(message).build();
        inputChannel.send(testMessage);
        Assertions.assertNull(MDC.get("traceId"));
        Assertions.assertNull(MDC.get("spanId"));
    }
}
