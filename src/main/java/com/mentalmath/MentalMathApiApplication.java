package com.mentalmath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Entry point for the Mental Math Game Spring Boot REST API.
 * Reuses existing JDBC and Game Engine business logic.
 */
@SpringBootApplication
public class MentalMathApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentalMathApiApplication.class, args);
    }

    /**
     * Configures global CORS mapping to allow local React development (port 5173 / 3000)
     * as well as deployed Vercel frontends.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns(
                                "http://localhost:5173",
                                "http://localhost:3000",
                                "http://127.0.0.1:5173",
                                "https://*.vercel.app",
                                "*"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}
