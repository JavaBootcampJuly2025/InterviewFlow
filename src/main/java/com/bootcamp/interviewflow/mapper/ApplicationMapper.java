package com.bootcamp.interviewflow.mapper;

import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

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
        if (dto.getApplyDate() != null) {
            app.setApplyDate(dto.getApplyDate());
        }
        if (dto.getInterviewDate() != null) {
            app.setInterviewDate(dto.getInterviewDate());
        }
        if (dto.getEmailNotificationsEnabled() != null) {
            app.setEmailNotificationsEnabled(dto.getEmailNotificationsEnabled());
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
                app.getApplyDate(),
                app.getCreatedAt(),
                app.getUpdatedAt(),
                app.getInterviewDate(),
                app.getEmailNotificationsEnabled()
        );
    }
}
