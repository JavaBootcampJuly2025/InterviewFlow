package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Optional<Resume> findByIdAndUserId(UUID id, Long userId);
    List<Resume> findAllByUser_Id(Long userId);
}
