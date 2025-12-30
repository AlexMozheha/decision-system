package com.risk.decision.client;


import com.risk.decision.dto.DecisionRequest;
import com.risk.decision.dto.DecisionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DecisionServiceClient {

    private final RestClient client;

    public DecisionServiceClient(RestClient.Builder builder) {
        this.client = builder.baseUrl("http://decision-service").build();
    }

    public DecisionResponse makeDecision(DecisionRequest request) {
        return client.post()
                .uri("/api/decisions/make-decision")
                .body(request)
                .retrieve()
                .body(DecisionResponse.class);
    }
}
