package com.example.flowershoptr.controller.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/render")
public class RenderController {

    @Value("${render.api.key}")
    private String renderApiKey;

    @Value("${render.service.id}")
    private String renderServiceId;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/restart")
    public ResponseEntity<?> restartServer() {
        try {
            String url = "https://api.render.com/v1/services/" + renderServiceId + "/restart";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + renderApiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(new HashMap<>(), headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Restart initiated",
                    "data", response.getBody()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to restart server: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/deploy")
    public ResponseEntity<?> deployLatestCommit() {
        try {
            String url = "https://api.render.com/v1/services/" + renderServiceId + "/deploys";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + renderApiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("clearCache", true); // Опционально: очистить кеш при деплое

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Deployment initiated",
                    "data", response.getBody()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to deploy: " + e.getMessage()
            ));
        }
    }
}