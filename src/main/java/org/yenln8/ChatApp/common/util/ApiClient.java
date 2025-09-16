package org.yenln8.ChatApp.common.util;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public String callPostExternalApi(String url, String requestBody,String token) {
        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // set Bearer Token

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Gửi POST
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }
}
