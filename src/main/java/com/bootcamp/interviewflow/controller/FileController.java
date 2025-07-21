package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.model.FileMetadata;
import com.bootcamp.interviewflow.security.CustomUserDetails;
import com.bootcamp.interviewflow.service.ObjectStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final ObjectStorageService storageService;

    public FileController(ObjectStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file) throws Exception {
        FileResponse response = storageService.upload(getCurrentUserId(), file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> download(@PathVariable UUID fileId) throws Exception {
        byte[] data = storageService.download(fileId, getCurrentUserId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<FileResponse> delete(@PathVariable UUID fileId) throws Exception {
        storageService.delete(fileId, getCurrentUserId());
        return ResponseEntity.ok(new FileResponse("File deleted", fileId));
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> list(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(storageService.findAllByUserId(userId));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }

        throw new IllegalStateException("Invalid principal type: " + principal.getClass());
    }

}
