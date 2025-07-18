package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;
import com.bootcamp.interviewflow.service.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "User notes related to job applications")
public class NotesController {
    private final NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @Operation(summary = "Create a new note for a job application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created",
                    content = @Content(schema = @Schema(implementation = NoteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<NoteResponse> create(@RequestBody NoteRequest request) {
        var response = notesService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a note by its Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note found",
                    content = @Content(schema = @Schema(implementation = NoteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@NotNull @PathVariable Long id) {
        var response = notesService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all notes for a specific job application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of notes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NoteResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid application ID",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllByApplicationId(@NotNull @RequestParam Long applicationId) {
        var response = notesService.getAllByApplicationId(applicationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a note by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted",
                    content = @Content(schema = @Schema(example = "Note with id 1001 deleted"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ApiResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
