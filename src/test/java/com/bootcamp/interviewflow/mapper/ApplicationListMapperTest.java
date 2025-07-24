package com.bootcamp.interviewflow.mapper;

import com.bootcamp.interviewflow.dto.ApplicationListResponse;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationListMapperTest {

    private final ApplicationListMapper mapper = new ApplicationListMapper();

    private LocalDateTime now;
    private User user;
    private Application app1, app2;
    private List<Application> applications;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = new User();
        user.setId(1L);

        app1 = new Application();
        app1.setId(1L);
        app1.setStatus(ApplicationStatus.APPLIED);
        app1.setCompanyName("Acme Corp");
        app1.setCompanyLink("https://acme.example");
        app1.setPosition("Software Engineer");
        app1.setCreatedAt(now);
        app1.setUpdatedAt(now);
        app1.setUser(user);

        app2 = new Application();
        app2.setId(2L);
        app2.setStatus(ApplicationStatus.REJECTED);
        app2.setCompanyName("Globex");
        app2.setCompanyLink("https://globex.example");
        app2.setPosition("Business Analyst");
        app2.setCreatedAt(now);
        app2.setUpdatedAt(now);
        app2.setUser(user);

        applications = List.of(app1, app2);
    }

    @Test
    void toApplicationListDTO_ShouldMapApplicationToDTO() {
        ApplicationListResponse dto = mapper.toApplicationListDTO(app1);

        assertThat(dto.id()).isEqualTo(app1.getId());
        assertThat(dto.status()).isEqualTo(app1.getStatus());
        assertThat(dto.companyName()).isEqualTo(app1.getCompanyName());
        assertThat(dto.companyLink()).isEqualTo(app1.getCompanyLink());
        assertThat(dto.position()).isEqualTo(app1.getPosition());
        assertThat(dto.createdAt()).isEqualTo(now);
        assertThat(dto.updatedAt()).isEqualTo(now);
    }

    @Test
    void toApplicationListDTOs_ShouldMapApplicationListToDTOList() {
        List<ApplicationListResponse> dtos = mapper.toApplicationListDTOs(applications);

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).id()).isEqualTo(1L);
        assertThat(dtos.get(0).companyName()).isEqualTo("Acme Corp");
        assertThat(dtos.get(1).id()).isEqualTo(2L);
        assertThat(dtos.get(1).companyName()).isEqualTo("Globex");
    }

    @Test
    void toApplicationListDTOs_ShouldReturnEmptyList_WhenInputIsEmpty() {
        List<ApplicationListResponse> dtos = mapper.toApplicationListDTOs(List.of());
        assertThat(dtos).isEmpty();
    }
}
