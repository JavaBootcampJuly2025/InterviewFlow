package com.bootcamp.interviewflow.mapper;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.model.Application;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationListMapper {

    public ApplicationListDTO toApplicationListDTO(Application application) {
        return new ApplicationListDTO(
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

    public List<ApplicationListDTO> toApplicationListDTOs(List<Application> applications) {
        return applications.stream()
                .map(this::toApplicationListDTO)
                .toList();
    }
}

