package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.FileMetadataResponse;
import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.model.Resume;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor
public class ResumeService implements ObjectStorageService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ObjectStorageService storageService;

    @Value("${resume.bucket:resumes}")
    private String bucketName;

    @Override
    public FileResponse upload(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is required");

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("Max size 5MB");

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return storageService.upload(userId, file);
    }


    public ResumeResponse uploadResume(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        FileResponse fileResponse = this.upload(userId, file);

        String originalFileName = file.getOriginalFilename();
        UUID fileId = fileResponse.fileId();
        String extension = Optional.ofNullable(FilenameUtils.getExtension(originalFileName)).orElse("");
        String objectKey = userId + "/" + fileId + (extension.isBlank() ? "" : "." + extension);

        Resume resume = Resume.builder()
                .id(fileId)
                .user(user)
                .fileName(originalFileName)
                .objectKey(objectKey)
                .fileSize(file.getSize())
                .build();
        resume = resumeRepository.save(resume);

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getObjectKey(),
                resume.getFileSize()
        );
    }


    @Override
    public byte[] download(UUID fileId, Long userId) {
        return storageService.download(fileId, userId);
    }

    @Override
    public void delete(UUID fileId, Long userId) {
        storageService.delete(fileId, userId);
    }

    @Override
    public List<FileMetadataResponse> findAllByUserId(Long userId) {
        return storageService.findAllByUserId(userId);
    }
}
