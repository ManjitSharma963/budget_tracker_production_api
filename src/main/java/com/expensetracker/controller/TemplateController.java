package com.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllTemplates() {
        // Stub endpoint - returns empty list for now
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getTemplateById(@PathVariable Long id) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Template feature not yet implemented");
        error.put("message", "This endpoint is planned for future implementation");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(error);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createTemplate(@RequestBody Map<String, Object> request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Template feature not yet implemented");
        error.put("message", "This endpoint is planned for future implementation");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(error);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateTemplate(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Template feature not yet implemented");
        error.put("message", "This endpoint is planned for future implementation");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(error);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTemplate(@PathVariable Long id) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Template feature not yet implemented");
        error.put("message", "This endpoint is planned for future implementation");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(error);
    }
}

