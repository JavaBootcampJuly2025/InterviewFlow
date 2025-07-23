package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.exception.FileNotFoundOrNoAccessException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.model.Resume;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class ResumeService implements ObjectStorageService {
    protected static final String FAILED_TO_DELETE_FILE = "Failed to delete file";
    protected static final String FAILED_TO_DOWNLOAD_FILE = "Failed to download file";
    protected static final String FAILED_TO_UPLOAD_FILE = "Failed to upload file";
    protected static final String FILE_IS_REQUIRED = "File is required";
    protected static final String MAX_FILE_SIZE_5_MB_IS_ALLOWED = "Max file size 5MB is allowed";
    protected static final String NO_ACCESS_OR_FILE = "No access or file";
    protected static final String ONLY_PDF_IS_ALLOWED = "Only PDF is allowed";
    protected static final String USER_NOT_FOUND = "User not found";

    protected static final int ALLOWED_SIZE = 5 * 1024 * 1024;

    private final MinioClient minioClient;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Value("${minio.bucket}")
    private String bucketName;

    @Override
    public ResumeResponse upload(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (file == null || file.isEmpty()) throw new IllegalArgumentException(FILE_IS_REQUIRED);

        String extension = Optional.ofNullable(FilenameUtils.getExtension(file.getOriginalFilename())).orElse("");
        if (!extension.endsWith("pdf")) {
            throw new IllegalArgumentException(ONLY_PDF_IS_ALLOWED);
        }

        if (file.getSize() > ALLOWED_SIZE) throw new IllegalArgumentException(MAX_FILE_SIZE_5_MB_IS_ALLOWED);

        UUID fileId = UUID.randomUUID();
        String objectKey = "resumes/user_" + userId + "/" + fileId + "." + extension;

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

        Resume resume = Resume.builder()
                .id(fileId)
                .user(user)
                .fileName(file.getOriginalFilename())
                .objectKey(objectKey)
                .size(file.getSize())
                .build();
        resume = resumeRepository.save(resume);

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getSize()
        );
    }

    @Override
    public byte[] download(UUID fileId, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundOrNoAccessException(NO_ACCESS_OR_FILE));

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(resume.getObjectKey()).build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public void delete(UUID fileId, Long userId) {
        Resume resume = resumeRepository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundOrNoAccessException(NO_ACCESS_OR_FILE));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(resume.getObjectKey()).build()
            );
            resumeRepository.delete(resume);
        } catch (Exception e) {
            throw new RuntimeException(FAILED_TO_DELETE_FILE, e);
        }
    }

    @Override
    public List<ResumeResponse> findAllByUserId(Long userId) {
        return resumeRepository.findAllByUser_Id(userId).stream()
                .map(resume -> new ResumeResponse(
                        resume.getId(),
                        resume.getFileName(),
                        resume.getSize()
                ))
                .sorted(Comparator.comparing(ResumeResponse::fileId))
                .collect(Collectors.toList());
    }
}
