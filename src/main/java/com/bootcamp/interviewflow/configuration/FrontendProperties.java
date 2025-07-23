package com.bootcamp.interviewflow.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "interviewflow.frontend")
public class FrontendProperties {
    private String frontendDomain;
    private String frontendPort;

}
