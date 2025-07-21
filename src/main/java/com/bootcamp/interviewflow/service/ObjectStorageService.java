package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.FileMetadataResponse;
import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for handling object storage operations such as uploading, downloading,
 * deleting files and retrieving file metadata.
 */
public interface ObjectStorageService {

    /**
     * Uploads a file for a specific user.
     *
     * @param userId the ID of the user uploading the file
     * @param file the file to be uploaded
     * @return a {@link FileResponse} containing metadata about the uploaded file
     * @throws Exception if the upload fails
     */
    FileResponse upload(Long userId, MultipartFile file) throws Exception;

    /**
     * Downloads a file for a specific user.
     *
     * @param fileId the UUID of the file to download
     * @param userId the ID of the user requesting the file
     * @return the file content as a byte array
     * @throws Exception if the file cannot be found or access is denied
     */
    byte[] download(UUID fileId, Long userId) throws Exception;

    /**
     * Deletes a file for a specific user.
     *
     * @param fileId the UUID of the file to delete
     * @param userId the ID of the user requesting the deletion
     * @throws Exception if the file cannot be deleted or access is denied
     */
    void delete(UUID fileId, Long userId) throws Exception;

    /**
     * Retrieves all metadata for files uploaded by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of {@link FileMetadataResponse} objects
     */
    List<FileMetadataResponse> findAllByUserId(Long userId);
}
