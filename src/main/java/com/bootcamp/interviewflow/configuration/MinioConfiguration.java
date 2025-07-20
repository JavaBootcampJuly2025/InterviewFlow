package com.bootcamp.interviewflow.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class MinioConfiguration {
    @Bean
    public MinioClient minioClient(@Value("${minio.url}") String minioUrl,
                                   @Value("${minio.port}") int minioPort,
                                   @Value("${minio.tlsOn}") boolean tlsOn,
                                   @Value("${minio.accessKey}") String accessKey,
                                   @Value("${minio.secretKey}") String secretKey) {
        return MinioClient.builder()
                .endpoint(minioUrl, minioPort, tlsOn)
                .credentials(accessKey, secretKey)
                .build();
    }
}
