package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for handling object storage operations such as uploading, downloading,
 * deleting files and retrieving file metadata.
 */
public interface ObjectStorageService {

    String FAILED_TO_DELETE_FILE = "Failed to delete file";
    String FAILED_TO_DOWNLOAD_FILE = "Failed to download file";
    String FAILED_TO_READ_FILE = "Failed to read file: ";
    String FAILED_TO_UPLOAD_FILE = "Failed to upload file";
    String FILE_IS_REQUIRED = "File is required";
    String MAX_FILE_SIZE_5_MB_IS_ALLOWED = "Max file size 5MB is allowed";
    String NO_ACCESS_OR_FILE = "No access or file";
    String ONLY_PDF_IS_ALLOWED = "Only PDF is allowed";
    String USER_NOT_FOUND = "User not found";
    int ALLOWED_SIZE = 5 * 1024 * 1024;

    /**
     * Uploads a file for a specific user.
     *
     * @param userId the ID of the user uploading the file
     * @param file   the file to be uploaded
     * @return a {@link ResumeResponse} containing metadata about the uploaded file
     */
    ResumeResponse upload(Long userId, MultipartFile file);

    /**
     * Downloads a file for a specific user.
     *
     * @param fileId the UUID of the file to download
     * @param userId the ID of the user requesting the file
     * @return the file content as a byte array
     */
    byte[] download(UUID fileId, Long userId);

    /**
     * Deletes a file for a specific user.
     *
     * @param fileId the UUID of the file to delete
     * @param userId the ID of the user requesting the deletion
     */
    void delete(UUID fileId, Long userId);

    /**
     * Retrieves all metadata for files uploaded by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of {@link ResumeResponse} objects
     */
    List<ResumeResponse> findAllByUserId(Long userId);
}
