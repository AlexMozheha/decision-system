package com.risk.client;

import com.risk.dto.CalculationRequest;
import com.risk.dto.CalculationResponse;

public interface CalculationServiceClient {

    CalculationResponse calculate(CalculationRequest request);
}
