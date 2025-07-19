package com.bootcamp.interviewflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;
import java.net.URL;

public class ValidWebUrlImpl implements ConstraintValidator<ValidWebUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }


        if (value.trim().isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(value);


            String protocol = url.getProtocol();
            if (!"http".equals(protocol) && !"https".equals(protocol)) {
                return false;
            }


            String host = url.getHost();
            if (host == null || host.trim().isEmpty()) {
                return false;
            }


            if (host.equals(".") || host.startsWith(".") || host.endsWith(".")) {
                return false;
            }


            if (host.contains("..")) {
                return false;
            }


            return host.equals("localhost") || host.contains(".");

        } catch (MalformedURLException e) {
            return false;
        }
    }
}