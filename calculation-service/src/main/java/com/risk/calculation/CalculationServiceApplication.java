package com.risk.calculation;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CalculationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculationServiceApplication.class, args);

        log.info("Calculation Service Application started successfully.");
    }
}