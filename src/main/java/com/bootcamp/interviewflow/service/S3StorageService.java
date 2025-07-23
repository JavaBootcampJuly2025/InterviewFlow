package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.UncheckedIOException;

@Profile("prod")
@Slf4j
@Service
public class S3StorageService extends AbstractStorageService {
    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(ResumeRepository resumeRepository,
                            UserRepository userRepository,
                            S3Client s3Client,
                            @Value("${aws.s3.bucket}") String bucketName) {
        super(resumeRepository, userRepository);
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void init() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("S3 bucket is accessible: {}", bucketName);
        } catch (S3Exception e) {
            log.error("S3 error: {}", e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (Exception e) {
            log.error("Unexpected error while checking bucket: {}", e.getMessage());
            System.exit(1);
        }
    }

    @Override
    protected void uploadFile(String objectKey, MultipartFile file) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        try {
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException(FAILED_TO_READ_FILE, e);
        }
    }

    @Override
    protected byte[] downloadFile(String objectKey) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        return s3Client.getObjectAsBytes(getRequest).asByteArray();
    }

    @Override
    protected void deleteFile(String objectKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.deleteObject(deleteRequest);
    }
}
