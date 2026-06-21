package com.risk.api.validation;

import java.math.BigDecimal;

public interface EvalValueForValidation {

    BigDecimal rawValue();
    BigDecimal score();
}
