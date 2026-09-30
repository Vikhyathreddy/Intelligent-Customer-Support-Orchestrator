package com.supportorchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SupportOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportOrchestratorApplication.class, args);
    }
}
