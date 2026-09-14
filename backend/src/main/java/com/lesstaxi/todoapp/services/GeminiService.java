package com.lesstaxi.todoapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String generateSubtasks(String title, String description) {
        String prompt = "You are a helpful productivity assistant. " +
                "I am creating a task titled: '" + title + "'. " +
                "Description: '" + description + "'. " +
                "Please break this task down into 3-5 actionable subtasks. " +
                "Output ONLY a markdown checklist (e.g. - [ ] Subtask 1). Do not include any intro or outro text.";

        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        
        content.put("parts", parts);
        contents.add(content);
        
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            String fullUrl = apiUrl + "?key=" + apiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, request, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> resParts = (List<Map<String, Object>>) contentMap.get("parts");
                    if (resParts != null && !resParts.isEmpty()) {
                        return (String) resParts.get(0).get("text");
                    }
                }
            }
            return "Failed to generate subtasks. Please try again.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while contacting AI assistant: " + e.getMessage();
        }
    }
}
