package com.bootcamp.interviewflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidWebUrlImpl.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWebUrl {
    String message() default "Company link must be a valid URL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}