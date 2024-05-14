package com.fitness.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SocialFitnessBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialFitnessBackEndApplication.class, args);
    }

}
