package com.risk.validation;

import java.math.BigDecimal;

public interface EvalValueForValidation {

    BigDecimal rawValue();
    BigDecimal score();
}
