package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.FileMetadataResponse;
import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.security.UserPrincipal;
import com.bootcamp.interviewflow.service.ObjectStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@Tag(name = "File Management", description = "Upload, download, delete and list user files")
public class FileController {

    private final ObjectStorageService storageService;

    public FileController(ObjectStorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "Upload a file", description = "Uploads a file for the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = FileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        FileResponse response = storageService.upload(userPrincipal.getId(), file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Download a file", description = "Downloads the file with the specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> download(@PathVariable UUID fileId,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        byte[] data = storageService.download(fileId, userPrincipal.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Operation(summary = "Delete a file", description = "Deletes the file with the specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File deleted successfully",
                    content = @Content(schema = @Schema(implementation = FileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<FileResponse> delete(@PathVariable UUID fileId,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        storageService.delete(fileId, userPrincipal.getId());
        return ResponseEntity.ok(new FileResponse("File deleted", fileId));
    }

    @Operation(summary = "List user files", description = "Lists all files uploaded by the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File list retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FileMetadataResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<FileMetadataResponse>> list(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(storageService.findAllByUserId(userPrincipal.getId()));
    }
}
