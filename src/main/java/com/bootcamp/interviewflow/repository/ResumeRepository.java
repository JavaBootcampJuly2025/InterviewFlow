package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
