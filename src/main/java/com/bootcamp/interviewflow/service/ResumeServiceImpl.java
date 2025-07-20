package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.model.Resume;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Value("${resume.upload-dir:/tmp/uploads/}")
    private String storageDir;

    @Override
    public ResumeResponse uploadResume(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is required");

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("Max size 5MB");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        File dir = new File(storageDir);
        if (!dir.exists()) dir.mkdirs();

        String storagePath = storageDir + System.currentTimeMillis() + "-" + originalFileName;
        try {
            file.transferTo(new File(storagePath));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to store file", ex);
        }

        Resume resume = Resume.builder()
                .user(user)
                .fileName(originalFileName)
                .fileUrl(storagePath)
                .fileSize(file.getSize())
                .build();
        resume = resumeRepository.save(resume);

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileUrl(),
                resume.getFileSize()
        );
    }
}

