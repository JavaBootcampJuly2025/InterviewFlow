package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.security.UserPrincipal;
import com.bootcamp.interviewflow.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "User job applications for employment")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Create a new job application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
    })
    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@RequestBody @Valid CreateApplicationRequest dto,
                                                      @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ApplicationResponse created = applicationService.create(dto, userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    //TODO: Should be moved to AdminApplicationController?
    @Operation(summary = "Get all job applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of applications",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicationListDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
    })
    @GetMapping("/all")
    public ResponseEntity<List<ApplicationListDTO>> findAll() {
        List<ApplicationListDTO> all = applicationService.findAll();
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "Get all applications submitted by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's job applications",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicationListDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ApplicationListDTO>> getUserApplications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(applicationService.findAllByUserId(userPrincipal.getId()));
    }

    @Operation(summary = "Delete a job application by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application deleted",
                    content = @Content(schema = @Schema(example = "Application with id 1001 deleted"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        applicationService.delete(id, userPrincipal.getId());
        return ResponseEntity.ok("Application with id " + id + " deleted");
    }

    @Operation(summary = "Partially update a job application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Application not found",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponse> partialUpdate(
            @PathVariable Long id,
            @RequestBody @Valid UpdateApplicationRequest dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(applicationService.partialUpdate(id, userPrincipal.getId(), dto));
    }
}