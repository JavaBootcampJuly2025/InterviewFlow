package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "User job applications for employment")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Create a new job application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created",
                    content = @Content(schema = @Schema(implementation = Application.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/applications")
    public ResponseEntity<Application> create(@RequestBody @Valid CreateApplicationRequest dto) {
        Application created = applicationService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Operation(summary = "Get all job applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of applications",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Application.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> findAll() {
        List<Application> all = applicationService.findAll();
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Get all applications submitted by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's job applications",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicationListDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping(path = "/users/{userId}/applications")
    public ResponseEntity<List<ApplicationListDTO>> getUserApplications(@PathVariable Long userId) {
        return ResponseEntity.ok(applicationService.findAllByUserId(userId));
    }

    @Operation(summary = "Delete a job application by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Application not found", content = @Content)
    })
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        applicationService.delete(id);
        return ResponseEntity.ok("Application with id " + id + " deleted");
    }
}
