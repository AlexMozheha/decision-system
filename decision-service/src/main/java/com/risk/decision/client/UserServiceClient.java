package com.risk.decision.client;

import com.risk.api.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceClient {

    private final RestClient client;

    public UserServiceClient(RestClient.Builder clientBuilder, @Value("${app.services.user-service-url}") String baseUrl) {
        this.client = clientBuilder.baseUrl(baseUrl).build();
    }

    public UserResponse getUserByLogin(String login) {
        try {

            return client.get()
                    .uri("/api/users/internal/{login}", login)
                    .retrieve()
                    .body(UserResponse.class);

        } catch (HttpClientErrorException e) {
            log.error("Error calling user-service (Status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());

            throw new RuntimeException("User service returned error: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<UserResponse> getUserByName(String name) {
        try {

            return client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/users/search/users")
                            .queryParam("name", name)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserResponse>>() {});

        } catch (HttpClientErrorException e) {
            log.error("Error calling user-service (Status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());

            throw new RuntimeException("User service returned error: " + e.getResponseBodyAsString(), e);
        }
    }

    public Map<Integer, String> getUsersNamesByIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return client.post()
                    .uri("/api/users/internal/batch-names")
                    .body(userIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<Integer, String>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch user names batch", e);
            return Collections.emptyMap();
        }
    }

}
