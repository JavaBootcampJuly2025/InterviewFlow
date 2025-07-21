package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.model.FileMetadata;
import com.bootcamp.interviewflow.repository.FileMetadataRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Profile("dev")
@Service
public class MinioStorageService implements ObjectStorageService {
    protected static final String NO_ACCESS_OR_FILE = "No access or file";

    private final MinioClient minioClient;
    private final FileMetadataRepository metadataRepo;
    private final String bucket;

    public MinioStorageService(MinioClient minioClient,
                               FileMetadataRepository metadataRepo,
                               @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.metadataRepo = metadataRepo;
        this.bucket = bucket;
    }

    @PostConstruct
    public void init() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @Override
    public FileResponse upload(Long userId, MultipartFile file) throws Exception {
        UUID fileId = UUID.randomUUID();
        String extension = Optional.ofNullable(FilenameUtils.getExtension(file.getOriginalFilename())).orElse("");
        String objectKey = userId + "/" + fileId + (extension.isBlank() ? "" : "." + extension);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .stream(file.getInputStream(), -1, 10485760)
                        .contentType(file.getContentType())
                        .build()
        );

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

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(metadata.getObjectKey()).build())) {
            return stream.readAllBytes();
        }
    }

    @Override
    public void delete(UUID fileId, Long userId) throws Exception {
        FileMetadata metadata = metadataRepo.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundException(NO_ACCESS_OR_FILE));

        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucket).object(metadata.getObjectKey()).build()
        );

        metadataRepo.delete(metadata);
    }

    @Override
    public List<FileMetadata> findAllByUserId(Long userId) {
        return metadataRepo.findAllByUserId(userId);
    }
}
