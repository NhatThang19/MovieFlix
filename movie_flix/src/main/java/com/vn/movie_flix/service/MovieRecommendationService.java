package com.vn.movie_flix.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MovieRecommendationService {

    @Value("${flask.api.url:http://localhost:5000}")
    private String flaskApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> getContentBasedRecommendations(Long movieId) {
        String url = flaskApiUrl + "/recommend/content?movie_id=" + movieId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("recommended_movies")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> movies = (List<Map<String, Object>>) body.get("recommended_movies");
                    return movies;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi gọi Flask API content-based: " + e.getMessage());
        }

        return List.of();
    }

    public List<Map<String, Object>> getPersonalizedRecommendations(Long userId) {
        String url = flaskApiUrl + "/recommend/personalized?user_id=" + userId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("recommended_movies")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> movies = (List<Map<String, Object>>) body.get("recommended_movies");
                    return movies;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi gọi Flask API personalized: " + e.getMessage());
        }

        return List.of();
    }

}