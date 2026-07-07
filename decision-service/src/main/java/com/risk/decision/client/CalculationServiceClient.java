package com.risk.decision.client;

import com.risk.api.dto.CalculationRequest;

import com.risk.api.dto.CalculationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class CalculationServiceClient {

    private final RestClient client;

    public CalculationServiceClient(RestClient.Builder builder, @Value("${app.services.calculation-service-url}") String baseUrl) {
        this.client = builder.clone().baseUrl(baseUrl).build();
    }
    public CalculationResponse calculate(CalculationRequest request) {
        try {
            return client.post()
                    .uri("/api/calculations")
                    .body(request)
                    .retrieve()
                    .body(CalculationResponse.class);
        }
        catch (HttpClientErrorException e) {
            log.error("Error calling calculation-service (Status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());

            throw new RuntimeException("Calculation service returned error: " + e.getResponseBodyAsString(), e);
        }
    }
}