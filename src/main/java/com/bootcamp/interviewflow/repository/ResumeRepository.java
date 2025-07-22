package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByObjectKey(String objectKey);
}
