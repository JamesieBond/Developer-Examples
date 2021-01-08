package com.tenx.universalbanking.interestcalculator.model.validators;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Inherited
@Target({PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = FutureDateValidator.class)
@Documented
public @interface FutureDate {

  String message() default "Interest Application Cycle's toDate cannot be a future date.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
