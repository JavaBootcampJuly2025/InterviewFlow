package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.service.ApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ApplicationService service;

    @Test
    @DisplayName("GET /api/users/{userId}/applications → 200 + JSON array")
    void getApplications_forUser_returnsDtoList() throws Exception {
        Long userId = 42L;
        LocalDateTime now = LocalDateTime.of(2025, 7, 16, 12, 0);
        var dto = new ApplicationListDTO(
                1L, ApplicationStatus.APPLIED, "Acme", "https://acme", "Eng", now, now
        );
        when(service.findAllByUserId(userId)).thenReturn(List.of(dto));

        mvc.perform(get("/api/users/{userId}/applications", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("APPLIED"))
                .andExpect(jsonPath("$[0].companyName").value("Acme"))
                .andExpect(jsonPath("$[0].position").value("Eng"));
    }

}
