package com.tracer;

import com.tracer.interceptors.TracingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

/**
 * @author doneskandari@gmail.com
 * Date    2023-10-12 23:50:23
 */

@EnableIntegration
@Configuration
@ComponentScan(basePackages = "com.tracer.*")
@PropertySource("classpath:application.properties")
public class IntegrationTestConfig {

    @Bean
    public MessageChannel inputChannel(TracingInterceptor tracingInterceptor) {
        DirectChannel directChannel = new DirectChannel();
        directChannel.addInterceptor(tracingInterceptor);
        return directChannel;
    }

    @Bean
    public MessageService messageService() {
        return new MessageService();
    }

    @Bean
    public IntegrationFlow successTestFlow() {
        return IntegrationFlows.from("inputChannel")
                .handle("messageService", "testActivator")
                .channel("outputChannel")
                .get();
    }
}
