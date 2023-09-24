# Distributed Tracing with Spring Integration, OpenTelemetry, and Zipkin

## Table of Contents

1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Configuration](#configuration)
3. [Usage](#usage)
3. [Context Propagation](#Context)

## Introduction

This repository is a specialized interceptor designed to facilitate the smooth integration of OpenTelemetry and Zipkin with Spring Integration. This extension simplifies the process of instrumenting Spring Integration-based applications, making it easier to capture and analyze telemetry data for enhanced observability.

## Getting Started

### Prerequisites

You need to have Zipkin installed in your environment, which you can do by following the instructions provided in this link: https://zipkin.io/pages/quickstart.html.

### Configuration

Follow the steps below to configure KTracer in your project:

1. Clone the repository using the command below:
```shell
git clone <repository_url>
```
Replace <repository_url> with the URL of the KTracer repository.
2. Then Navigate through the directory:
```shell
cd  <project_directory>
```
Replace <project_directory> with the path to the cloned KTracer repository.
3. Install the KTracer with Maven:
```shell
mvn clean install  
```
4. Add Dependency to `pom.xml`:
```xml
<dependency>
    <groupId>com.tracer</groupId>
	<artifactId>ktracer</artifactId>
	<version>1.0.0</version>
</dependency>
```
5. In your Spring Boot main class, add the following annotation to enable component scanning for KTracer packages:
```java
@SpringBootApplication(scanBasePackages = {"com.tracer.*"})
```
This ensures that KTracer components are recognized and used in your application.
6. Add the following properties to your configuration file (e.g., application.properties or application.yml) and customize them accordingly:

For application.properties:
```properties
this.service:application-name
tracer.name:tracer-name
zipkin.endpoint:zipkin-endpoit-address
```
For application.yml:
```yaml
this:
  service: application-name
tracer:
  name: tracer-name
zipkin:
  endpoint: zipkin-endpoint-address
```

## Usage

Here are the usage instructions for integrating KTracer into your Spring Boot application:

To leverage KTracer in your Spring Boot application, follow these steps:

1. Create an HTTP Inbound Gateway:

Create an HTTP Inbound Gateway in your Spring Boot application. This gateway will handle incoming HTTP requests and serve as the entry point to your application. Ensure that it is properly configured to receive incoming requests.

```xml
 <int-http:inbound-gateway request-channel="your-preferred-channel"
                              supported-methods="POST,GET"
                              path="/{var}">
</int-http:inbound-gateway>
```
2. Create an Interceptor and Use TracingInterceptor:

Create an interceptor that will intercept incoming HTTP requests. You can use KTracer's TracingInterceptor to automatically instrument your application for tracing.

```xml
<int:channel id="your-preferred-channel">
        <int:interceptors>
            <int:ref bean="tracingInterceptor"/>
        </int:interceptors>
</int:channel>
``` 

With these steps completed, KTracer will automatically intercept incoming HTTP requests to your Spring Boot application and instrument them for tracing. This instrumentation will help you monitor and trace the flow of requests through your application for performance analysis and debugging.

## Context Propagation within Your Distributed System using KTracer

To facilitate context propagation within your distributed system when using KTracer, follow these steps:

1. Add KTracer Interceptor:

Ensure that you have added the KTracer interceptor to your Spring Boot application, as mentioned earlier. This interceptor will handle tracing and span management.

2. Access Current Span:

Within your application, you can access the current span using the SpanHolder class, which provides thread-local storage for the current span. You can use the get, set, and remove methods of SpanHolder to interact with the current span.

```java
Span currentSpan = SpanHolder.get();
```

3. Create SpanContext:

To propagate the tracing context to other services, you can create a SpanContext object. This object should store the following information:

`spanId`
`traceId`
`traceFlags`
`traceState`

You can extract these values from the current span you obtained in the previous step.

```java
SpanContext spanContext = span.getSpanContext();
```

4. Add Headers in Outgoing Requests:

When making HTTP requests to other services within your distributed system using an HTTP client like RestTemplate, OkHttp, etc., add the following three headers to your outgoing requests:

`x-trace-id`: Set this header with the trace ID from the SpanContext.
   
`x-span-id`: Set this header with the span ID from the SpanContext.
    
`x-trace-flags`: Set this header with the trace flags from the SpanContext.

By adding these headers, you are propagating the tracing context to the downstream services.

Example in RestTemplate:

```java
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.set("x-trace-id", spanContext.getTraceId());
headers.set("x-span-id", spanContext.getSpanId());
headers.set("x-trace-flags", spanContext.getTraceFlags());

HttpEntity<String> entity = new HttpEntity<>(headers);
ResponseEntity<String> response = restTemplate.exchange(
    "http://other-service-url",
    HttpMethod.GET,
    entity,
    String.class
);
```
5. Automatic Child Span Generation:

If the other services within your distributed system also use KTracer and are configured properly, KTracer will automatically identify the parent request based on the headers (x-trace-id, x-span-id, x-trace-flags) you added to your request. KTracer will generate a child span for that request, allowing you to trace the entire flow across services.
