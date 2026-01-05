package com.expensetracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Allow specific origins for development (explicit origins required when credentials are enabled)
        config.addAllowedOrigin("http://localhost");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("http://localhost:8080");
        
        // For production, add your production domain here
        // config.addAllowedOrigin("https://yourdomain.com");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow all HTTP methods
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");
        
        // Expose headers
        config.addExposedHeader("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

