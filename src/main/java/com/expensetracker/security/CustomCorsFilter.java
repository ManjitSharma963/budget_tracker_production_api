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

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        // 🔥 BYPASS health checks completely
        if (path.equals("/health") || path.equals("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = request.getHeader("Origin");
        String requestMethod = request.getMethod();
        
        // Log for debugging
        logger.info("CORS Filter: " + requestMethod + " " + request.getRequestURI() + " | Origin: " + origin);
        
        // Handle preflight OPTIONS requests FIRST - must be before filterChain
        if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
            // Always allow OPTIONS requests from ANY origin
            if (origin != null && !origin.isEmpty()) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "*");
                response.setHeader("Access-Control-Max-Age", "3600");
                logger.info("CORS: OPTIONS preflight - Headers set for origin: " + origin);
            } else {
                // Even if no origin, set basic CORS headers
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "*");
                logger.warn("CORS: OPTIONS preflight - No origin header present");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Don't continue filter chain for OPTIONS
        }
        
        // Allow ALL origins for actual requests - set CORS headers for any origin
        // When allowCredentials is true, we must use the actual origin value, not "*"
        if (origin != null && !origin.isEmpty()) {
            // Always allow any origin - echo back the origin that was sent
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Expose-Headers", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        filterChain.doFilter(request, response);
    }
}

