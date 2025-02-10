package com.charlie.truproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class TruProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(TruProxyApplication.class, args);
    }
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
