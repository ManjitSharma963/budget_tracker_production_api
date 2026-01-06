package com.expensetracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter extends OncePerRequestFilter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "http://localhost",
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4200",
        "http://localhost:8080",
        "http://139.59.85.102",
        "http://139.59.85.102:80",
        "http://139.59.85.102:8080",
        "http://www.trackmyexpenses.in",
        "https://www.trackmyexpenses.in",
        "http://trackmyexpenses.in",
        "https://trackmyexpenses.in"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        String requestMethod = request.getMethod();
        
        // Log all requests for debugging
        logger.debug("CORS Filter: " + requestMethod + " " + request.getRequestURI() + " from origin: " + origin);
        
        // Check if origin is allowed (case-insensitive)
        boolean isAllowedOrigin = false;
        if (origin != null) {
            String normalizedOrigin = origin.toLowerCase().trim();
            isAllowedOrigin = ALLOWED_ORIGINS.stream()
                .anyMatch(allowed -> allowed.toLowerCase().equals(normalizedOrigin));
        }
        
        // Handle preflight OPTIONS requests FIRST - must be before filterChain
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            // Always set CORS headers for OPTIONS if origin is allowed
            if (isAllowedOrigin && origin != null) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "*");
                response.setHeader("Access-Control-Max-Age", "3600");
                logger.info("CORS: OPTIONS preflight ALLOWED for origin: " + origin);
            } else {
                // Log for debugging - this helps identify why CORS is failing
                if (origin != null) {
                    logger.error("CORS: OPTIONS request BLOCKED - Origin not allowed: " + origin);
                    logger.error("CORS: Allowed origins: " + ALLOWED_ORIGINS);
                    logger.error("CORS: Normalized origin: " + (origin != null ? origin.toLowerCase().trim() : "null"));
                } else {
                    logger.warn("CORS: OPTIONS request with no Origin header");
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Don't continue filter chain for OPTIONS
        }
        
        // Set CORS headers for allowed origins on actual requests
        if (isAllowedOrigin && origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Expose-Headers", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
        } else if (origin != null) {
            // Log unallowed origin for debugging
            logger.warn("CORS: Origin not allowed: " + origin + ". Allowed origins: " + ALLOWED_ORIGINS);
        }

        filterChain.doFilter(request, response);
    }
}

