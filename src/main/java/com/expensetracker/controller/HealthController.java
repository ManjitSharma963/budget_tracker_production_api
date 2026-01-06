package com.expensetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost", "http://localhost:5173", "http://localhost:3000", "http://localhost:4200", "http://139.59.85.102", "http://139.59.85.102:80", "http://139.59.85.102:8080", "http://www.trackmyexpenses.in", "https://www.trackmyexpenses.in", "http://trackmyexpenses.in", "https://trackmyexpenses.in"}, allowCredentials = "true")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "API is running");
        return ResponseEntity.ok(response);
    }
}

