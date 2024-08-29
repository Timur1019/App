package org.example.app.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatGptApiClient {

    private final RestTemplate restTemplate;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    public ChatGptApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translateText(String text) {
        // Подготовка тела запроса
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", text);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        // Подготовка заголовков
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создание HttpEntity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Выполнение запроса
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object choicesObj = response.getBody().get("choices");
            if (choicesObj instanceof List<?> choicesList) {
                if (!choicesList.isEmpty() && choicesList.get(0) instanceof Map<?, ?> choiceMap) {
                    Object textObj = choiceMap.get("text");
                    if (textObj instanceof String) {
                        return (String) textObj;
                    }
                }
            }
        }

        throw new RuntimeException("Ошибка при вызове ChatGPT API или неверный формат ответа");
    }
}