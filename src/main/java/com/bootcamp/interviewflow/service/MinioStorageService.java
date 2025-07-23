package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Profile("dev")
@Service
public class MinioStorageService extends AbstractStorageService {

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

    @Override
    protected void uploadFile(String objectKey, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), -1, file.getSize())
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_UPLOAD_FILE, e);
        }
    }

    @Override
    protected byte[] downloadFile(String objectKey) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectKey).build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_DOWNLOAD_FILE, e);
        }
    }

    @Override
    protected void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build()
            );
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_DELETE_FILE, e);
        }
    }
}
