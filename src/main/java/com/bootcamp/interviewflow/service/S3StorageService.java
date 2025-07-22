package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.FileMetadataResponse;
import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.model.FileMetadata;
import com.bootcamp.interviewflow.repository.FileMetadataRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("prod")
@Slf4j
@Service
public class S3StorageService implements ObjectStorageService {
    protected static final String NO_ACCESS_OR_FILE = "No access or file";

    private final S3Client s3Client;
    private final FileMetadataRepository metadataRepo;
    private final String bucketName;

    public S3StorageService(S3Client s3Client,
                            FileMetadataRepository metadataRepo,
                            @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.metadataRepo = metadataRepo;
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void init() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.error("S3 bucket exists and is accessible: {}", bucketName);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.error("S3 bucket does not exist: {}", bucketName);
            } else {
                log.error("Error accessing bucket: {}", e.awsErrorDetails().errorMessage());
            }
            System.exit(1);
        } catch (Exception e) {
            log.error("Unexpected error while checking bucket: {}", e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public FileResponse upload(Long userId, MultipartFile file) throws Exception {
        UUID fileId = UUID.randomUUID();
        String extension = Optional.ofNullable(FilenameUtils.getExtension(file.getOriginalFilename())).orElse("");
        String objectKey = "files/user_" + userId + "/" + fileId + (extension.isBlank() ? "" : "." + extension);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        FileMetadata metadata = new FileMetadata();
        metadata.setId(fileId);
        metadata.setUserId(userId);
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setContentType(file.getContentType());
        metadata.setObjectKey(objectKey);
        metadataRepo.save(metadata);

        return new FileResponse("File uploaded", fileId);
    }

    @Override
    public byte[] download(UUID fileId, Long userId) throws Exception {
        FileMetadata metadata = metadataRepo.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundException(NO_ACCESS_OR_FILE));

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(metadata.getObjectKey())
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getRequest);
        return objectBytes.asByteArray();
    }

    @Override
    public void delete(UUID fileId, Long userId) throws Exception {
        FileMetadata metadata = metadataRepo.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundException(NO_ACCESS_OR_FILE));

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(metadata.getObjectKey())
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    @Override
    public List<FileMetadataResponse> findAllByUserId(Long userId) {
        return metadataRepo.findAllByUserId(userId).stream()
                .map(metadata -> new FileMetadataResponse(
                        metadata.getId(),
                        metadata.getOriginalFilename(),
                        metadata.getContentType(),
                        metadata.getCreatedAt()
                ))
                .sorted(Comparator.comparing(FileMetadataResponse::id))
                .collect(Collectors.toList());
    }
}
