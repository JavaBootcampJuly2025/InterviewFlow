package com.bootcamp.interviewflow.mapper;

import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.Resume;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private final ResumeRepository resumeRepository;

    public Application updateEntityFromDto(UpdateApplicationRequest dto, Application app) {
        if (dto.getStatus() != null) {
            app.setStatus(dto.getStatus());
        }
        if (dto.getCompanyName() != null) {
            app.setCompanyName(dto.getCompanyName());
        }
        if (dto.getCompanyLink() != null) {
            app.setCompanyLink(dto.getCompanyLink());
        }
        if (dto.getPosition() != null) {
            app.setPosition(dto.getPosition());
        }
        if (dto.getLocation() != null) {
            app.setLocation(dto.getLocation());
        }
        if (dto.getApplyDate() != null) {
            app.setApplyDate(dto.getApplyDate());
        }
        if (dto.getInterviewDate() != null) {
            app.setInterviewDate(dto.getInterviewDate());
        }
        if (dto.getEmailNotificationsEnabled() != null) {
            app.setEmailNotificationsEnabled(dto.getEmailNotificationsEnabled());
        }
        if (dto.getResumeId() != null) {
            if (dto.getResumeId().trim().isEmpty()) {
                app.setResume(null);
            } else {
                try {
                    UUID resumeUuid = UUID.fromString(dto.getResumeId());
                    Resume resume = resumeRepository.findByIdAndUserId(resumeUuid, app.getUser().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Resume not found or does not belong to user"));
                    app.setResume(resume);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid resume ID format");
                }
            }
        }

        return app;
    }

    public ApplicationResponse toResponse(Application app) {
        return new ApplicationResponse(
                app.getId(),
                app.getStatus(),
                app.getCompanyName(),
                app.getCompanyLink(),
                app.getPosition(),
                app.getLocation(),
                app.getApplyDate(),
                app.getCreatedAt(),
                app.getUpdatedAt(),
                app.getInterviewDate(),
                app.getEmailNotificationsEnabled()
        );
    }
}
