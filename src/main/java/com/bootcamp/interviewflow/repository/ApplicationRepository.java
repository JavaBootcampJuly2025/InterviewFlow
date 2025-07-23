package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findAllByUserId(Long userId);

    List<Application> findAllByUserId(Long userId, Sort sort);

    Optional<Application> findByIdAndUserId(Long id, Long userId);

    List<Application> findAllByUserIdAndStatus(Long userId, ApplicationStatus status, Sort sort);
}
