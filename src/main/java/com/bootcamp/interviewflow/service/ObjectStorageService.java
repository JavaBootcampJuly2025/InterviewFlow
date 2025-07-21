package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ObjectStorageService {
    FileResponse upload(Long userId, MultipartFile file) throws Exception;

    byte[] download(UUID fileId, Long userId) throws Exception;

    void delete(UUID fileId, Long userId) throws Exception;

    List<FileMetadata> findAllByUserId(Long userId);
}
