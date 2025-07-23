package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.model.User;
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
    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadResume_shouldThrowIfFileIsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> resumeService.upload(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File is required");
    }

    @Test
    void uploadResume_shouldThrowIfFileIsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(file.isEmpty()).thenReturn(true);
        assertThatThrownBy(() -> resumeService.upload(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File is required");
    }

    @Test
    void uploadResume_shouldThrowIfNotPDF() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("resume.docx");
        assertThatThrownBy(() -> resumeService.upload(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ResumeService.ONLY_PDF_IS_ALLOWED);
    }

    @Test
    void uploadResume_shouldThrowIfTooBig() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("resume.pdf");
        when(file.getSize()).thenReturn(6L * 1024 * 1024); // 6MB
        assertThatThrownBy(() -> resumeService.upload(1L, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ResumeService.MAX_FILE_SIZE_5_MB_IS_ALLOWED);
    }

    @Test
    void uploadResume_shouldThrowIfUserNotFound() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("resume.pdf");
        when(file.getSize()).thenReturn(1024L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> resumeService.upload(1L, file))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

}
