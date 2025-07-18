package com.holt.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

//@ConditionalOnProperty(prefix = "sys",name = "rest-template-config",havingValue = "true")
@Configuration
@Slf4j
public class WebClientDefaultConfig {
    @Bean
    public WebClient webClient() {
        log.info("WebClientDefaultConfig init start ...");
        HttpClient httpClient = HttpClient.create();

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        log.info("WebClientDefaultConfig init end");
        return webClient;
    }
}
