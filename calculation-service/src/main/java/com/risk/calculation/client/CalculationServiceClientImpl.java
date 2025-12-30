package com.risk.calculation.client;


import com.risk.client.CalculationServiceClient;
import com.risk.dto.CalculationRequest;

import com.risk.dto.CalculationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class CalculationServiceClientImpl implements CalculationServiceClient {

    private final RestClient client;

    public CalculationServiceClientImpl(RestClient.Builder builder) {
        this.client = builder.baseUrl("http://calculation-service").build();
    }

    @Override
    public CalculationResponse calculate(CalculationRequest request) {
        try {
        return client.post()
                .uri("/api/decisions/calculate")
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