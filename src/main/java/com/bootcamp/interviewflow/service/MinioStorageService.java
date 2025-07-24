package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.exception.FileInteractionException;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Profile("dev")
@Service
public class MinioStorageService extends AbstractStorageService {
    protected static final int PART_SIZE = 5 * 1024 * 1024; // Minimum allowed part size according to Minio Specs

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioStorageService(ResumeRepository resumeRepository,
                               UserRepository userRepository,
                               MinioClient minioClient,
                               @Value("${minio.bucket}") String bucketName) {
        super(resumeRepository, userRepository);
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void init() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @Override
    protected void uploadFile(String objectKey, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), -1, PART_SIZE)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new FileInteractionException(FAILED_TO_UPLOAD_FILE);
        }
    }

    @Override
    protected byte[] downloadFile(String objectKey) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectKey).build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new FileInteractionException(FAILED_TO_DOWNLOAD_FILE);
        }
    }

    @Override
    protected void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build()
            );
        } catch (Exception e) {
            throw new FileInteractionException(FAILED_TO_DELETE_FILE);
        }
    }
}
