package com.bootcamp.interviewflow.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final String frontendDomain;

    public CorsConfig(@Value("${interviewFlow.frontend.frontendDomain}") String frontendDomain) {
        this.frontendDomain = frontendDomain;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String origin = "http://" + frontendDomain;

        registry.addMapping("/**")
                .allowedOrigins(origin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
