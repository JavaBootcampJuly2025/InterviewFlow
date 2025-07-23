package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.FileResponse;
import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.security.UserPrincipal;
import com.bootcamp.interviewflow.service.ObjectStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resumes", description = "Upload resume files")
public class ResumeController {

    private final ObjectStorageService objectStorageService;

    @Operation(summary = "Upload a resume (PDF only, max 5MB)", description = "Uploads a resume for the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resume uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ResumeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> uploadResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {
        ResumeResponse response = objectStorageService.upload(userPrincipal.getId(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Download a resume", description = "Downloads the resume with the specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume downloaded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> download(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID fileId) {
        byte[] data = objectStorageService.download(fileId, userPrincipal.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @Operation(summary = "Delete a resume", description = "Deletes the resume with the specified ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResumeResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<FileResponse> delete(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID fileId) {
        objectStorageService.delete(fileId, userPrincipal.getId());
        return ResponseEntity.ok(new FileResponse("Resume deleted", fileId));
    }

    @Operation(summary = "List user resumes", description = "Lists all resumes uploaded by the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume list retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResumeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<ResumeResponse>> list(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(objectStorageService.findAllByUserId(userPrincipal.getId()));
    }
}
