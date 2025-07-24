package com.bootcamp.interviewflow.dto;

import com.bootcamp.interviewflow.model.Note;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

@Schema(description = "Response representing a note linked to a job application")
public record NoteResponse(
        @Schema(description = "Unique ID of the note", example = "456")
        Long id,

        @Schema(description = "ID of the associated job application", example = "123")
        Long applicationId,

        @Schema(description = "Title of the note", example = "Call with recruiter")
        String title,

        @Schema(description = "Content of the note", example = "Follow up in a week.")
        String content,

        @Schema(description = "Tags for the note", example = "[\"urgent\",\"followup\"]")
        List<String> tags,

        @Schema(description = "When the note was created", example = "2025-07-17 13:45:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "When the note was last updated", example = "2025-07-17 14:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt

) {
    public static NoteResponse from(Note note) {
        List<String> tagsList = (note.getTags() != null && !note.getTags().isEmpty())
                ? Arrays.asList(note.getTags().split(","))
                : Collections.emptyList();
        return new NoteResponse(
                note.getId(),
                note.getApplication().getId(),
                note.getTitle(),
                note.getContent(),
                tagsList,
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
