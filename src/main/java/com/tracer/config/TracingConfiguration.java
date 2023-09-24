package com.tracer.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author doneskandari@gmail.com
 * Date    2023-09-24 00:30:06
 */

@Configuration
public class TracingConfiguration {
    public static final AttributeKey<String> SERVICE_NAME = AttributeKey.stringKey("service.name");

    @Value("${zipkin.endpoint}")
    private String zipkinEndpoint;

    @Value("${this.service}")
    private String serviceName;

    @Value("${tracer.name}")
    private String tracerName;

    @Bean
    public OpenTelemetry openTelemetry() {
        SpanExporter zipkinExporter = ZipkinSpanExporter.builder()
                .setEndpoint(this.zipkinEndpoint)
                .build();

        Resource resource = Resource.builder()
                .put(SERVICE_NAME, this.serviceName)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(zipkinExporter))
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }
    @Bean
    public Tracer tracer() {
        return openTelemetry().getTracer(this.tracerName);
    }
}
