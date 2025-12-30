package com.risk.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MutuallyExclusiveValidator implements ConstraintValidator<MutuallyExclusive, EvalValueForValidation> {

    @Override
    public boolean isValid(EvalValueForValidation eval, ConstraintValidatorContext context) {

        if (eval == null) {
            return true;
        }

        boolean rawValueProvided = eval.rawValue() != null;
        boolean scoreProvided = eval.score() != null;

        // Правило XOR: (rawValue є І score немає) АБО (rawValue немає І score є)
        return (rawValueProvided && !scoreProvided) || (!rawValueProvided && scoreProvided);
    }
}

