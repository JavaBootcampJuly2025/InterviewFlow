package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.exception.FileNotFoundOrNoAccessException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.model.Resume;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractStorageService implements ObjectStorageService {

    protected final ResumeRepository resumeRepository;
    protected final UserRepository userRepository;

    public AbstractStorageService(ResumeRepository resumeRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    protected abstract void uploadFile(String objectKey, MultipartFile file);
    protected abstract byte[] downloadFile(String objectKey);
    protected abstract void deleteFile(String objectKey);

    protected String buildObjectKey(Long userId, UUID fileId, String extension) {
        return "resumes/user_" + userId + "/" + fileId + (extension.isBlank() ? "" : "." + extension);
    }

    @Override
    public ResumeResponse upload(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        if (file == null || file.isEmpty()) throw new IllegalArgumentException(FILE_IS_REQUIRED);

        String extension = Optional.ofNullable(FilenameUtils.getExtension(file.getOriginalFilename())).orElse("");
        if (!extension.endsWith("pdf")) throw new IllegalArgumentException(ONLY_PDF_IS_ALLOWED);
        if (file.getSize() > ALLOWED_SIZE) throw new IllegalArgumentException(MAX_FILE_SIZE_5_MB_IS_ALLOWED);

        UUID fileId = UUID.randomUUID();
        String objectKey = buildObjectKey(userId, fileId, extension);

        uploadFile(objectKey, file);

        Resume resume = Resume.builder()
                .id(fileId)
                .user(user)
                .fileName(file.getOriginalFilename())
                .objectKey(objectKey)
                .size(file.getSize())
                .build();

        resume = resumeRepository.save(resume);

        return new ResumeResponse(resume.getId(), resume.getFileName(), resume.getSize());
    }

    @Override
    public byte[] download(UUID fileId, Long userId) {
        Resume resume = findResume(fileId, userId);
        return downloadFile(resume.getObjectKey());
    }

    @Override
    public void delete(UUID fileId, Long userId) {
        Resume resume = findResume(fileId, userId);
        deleteFile(resume.getObjectKey());
        resumeRepository.delete(resume);
    }

    @Override
    public List<ResumeResponse> findAllByUserId(Long userId) {
        return resumeRepository.findAllByUser_Id(userId).stream()
                .map(r -> new ResumeResponse(r.getId(), r.getFileName(), r.getSize()))
                .sorted(Comparator.comparing(ResumeResponse::fileId))
                .collect(Collectors.toList());
    }

    private Resume findResume(UUID fileId, Long userId) {
        return resumeRepository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new FileNotFoundOrNoAccessException(NO_ACCESS_OR_FILE));
    }
}
