package com.risk.decision;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@ComponentScan(basePackages = {"com.risk.decision", "com.risk.calculation.client"})
@Slf4j
public class DecisionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecisionServiceApplication.class, args);
        log.info("Decision Service Application started successfully.");
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}
