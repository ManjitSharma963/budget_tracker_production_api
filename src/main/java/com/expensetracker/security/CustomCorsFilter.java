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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter extends OncePerRequestFilter {

    // Allow all origins - no restrictions
    private static final boolean ALLOW_ALL_ORIGINS = true;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        String requestMethod = request.getMethod();
        
        // Allow ALL origins - set CORS headers for any origin
        // When allowCredentials is true, we must use the actual origin value, not "*"
        if (origin != null) {
            // Always allow any origin - echo back the origin that was sent
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Expose-Headers", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
        
        // Handle preflight OPTIONS requests FIRST - must be before filterChain
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            // Always allow OPTIONS requests from any origin
            if (origin != null) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "*");
                response.setHeader("Access-Control-Max-Age", "3600");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            logger.debug("CORS: OPTIONS preflight ALLOWED for origin: " + origin);
            return; // Don't continue filter chain for OPTIONS
        }

        filterChain.doFilter(request, response);
    }
}

