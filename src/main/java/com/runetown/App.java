package com.runetown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.runetown.lifeprogression.application.ProcessEvidenceCandidateService;
import com.runetown.lifeprogression.domain.evidence.RuleBasedQualificationPolicy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "RuneTown Companion API",
                version = "1.0",
                description = "Local Life Progression API"))
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    ProcessEvidenceCandidateService processEvidenceCandidateService() {
        return new ProcessEvidenceCandidateService(
                new RuleBasedQualificationPolicy());
    }
}
