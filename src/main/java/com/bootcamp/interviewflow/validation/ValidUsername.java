package com.bootcamp.interviewflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUsernameImpl.class)
@Documented
public @interface ValidUsername {
    String message() default "Username can only contain letters and spaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}