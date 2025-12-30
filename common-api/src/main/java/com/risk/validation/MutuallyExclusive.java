package com.risk.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MutuallyExclusiveValidator.class)
public @interface MutuallyExclusive {

    String message() default "You must provide either the (rawValue or the final (score), but not both and not neither.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
