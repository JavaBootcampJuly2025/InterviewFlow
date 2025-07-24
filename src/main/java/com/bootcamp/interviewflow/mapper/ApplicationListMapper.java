package com.bootcamp.interviewflow.mapper;

import com.bootcamp.interviewflow.dto.ApplicationListResponse;
import com.bootcamp.interviewflow.model.Application;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationListMapper {

    public ApplicationListResponse toApplicationListDTO(Application application) {
        return new ApplicationListResponse(
                application.getId(),
                application.getStatus(),
                application.getCompanyName(),
                application.getCompanyLink(),
                application.getPosition(),
                application.getLocation(),
                application.getApplyDate(),
                application.getCreatedAt(),
                application.getInterviewDate(),
                application.getEmailNotificationsEnabled(),
                application.getUpdatedAt()
        );
    }

    public List<ApplicationListResponse> toApplicationListDTOs(List<Application> applications) {
        return applications.stream()
                .map(this::toApplicationListDTO)
                .toList();
    }
}

