package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ResumeServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MultipartFile file;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadResume_shouldThrowIfFileIsNull() {
        assertThatThrownBy(() -> resumeService.uploadResume(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File is required");
    }

    @Test
    void uploadResume_shouldThrowIfFileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);
        assertThatThrownBy(() -> resumeService.uploadResume(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File is required");
    }

    @Test
    void uploadResume_shouldThrowIfNotPDF() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("resume.docx");
        assertThatThrownBy(() -> resumeService.uploadResume(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only PDF allowed");
    }

    @Test
    void uploadResume_shouldThrowIfTooBig() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("resume.pdf");
        when(file.getSize()).thenReturn(6L * 1024 * 1024); // 6MB
        assertThatThrownBy(() -> resumeService.uploadResume(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Max size 5MB");
    }

    @Test
    void uploadResume_shouldThrowIfUserNotFound() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("resume.pdf");
        when(file.getSize()).thenReturn(1024L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> resumeService.uploadResume(1L, file))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

}
