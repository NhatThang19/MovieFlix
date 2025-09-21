package com.vn.movie_flix.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    @Value("${flask.api.url:http://localhost:5000}")
    private String flaskApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void notifyDataChanged(String eventType, Long entityId) {
        String webhookUrl = flaskApiUrl + "/webhook/data-changed";

        try {

            Map<String, Object> payload = new HashMap<>();
            payload.put("entity_id", entityId);
            payload.put("event_type", eventType);
            payload.put("source", "spring-boot-app");

            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, payload, String.class);


        } catch (Exception e) {
            System.out.println("Có lỗi khi gửi webhook: " + e.getMessage());
        }
    }
}