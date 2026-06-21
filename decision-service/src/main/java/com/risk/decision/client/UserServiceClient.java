package com.risk.decision.client;

import com.risk.api.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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

}
