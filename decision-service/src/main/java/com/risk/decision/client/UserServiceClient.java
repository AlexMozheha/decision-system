package com.risk.decision.client;

import com.risk.api.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceClient {

    private final RestClient client;

    public UserServiceClient(RestClient.Builder loadBalancedRestClientBuilder) {
        this.client = loadBalancedRestClientBuilder.clone()
                .baseUrl("http://user-service")
                .requestInterceptor(this::addAuthorizationHeader)
                .build();
    }

    private ClientHttpResponse addAuthorizationHeader(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest currentRequest = attributes.getRequest();
            String authHeader = currentRequest.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                request.getHeaders().add("Authorization", authHeader);
            }
        }

        return execution.execute(request, body);
    }

    public UserResponse getUserByLogin(String login) {
        try {

            return client.get()
                    .uri("/api/internal/users/{login}", login)
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
                            .path("/api/users")
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
                    .uri("/api/internal/users/batch-names")
                    .body(userIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<Integer, String>>() {});
        } catch (Exception e) {
            log.error("Failed to fetch user names batch", e);
            return Collections.emptyMap();
        }
    }

    public UserResponse getUserById(Integer userId) {
        try {
            return client.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("User service returned error: " + e.getResponseBodyAsString(), e);
        }
    }

}
