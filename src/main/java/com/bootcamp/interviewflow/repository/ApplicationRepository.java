package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByUserId(Long userId);

    Optional<Application> findByIdAndUserId(Long id, Long userId);

    // Фильтрация по статусу для пользователя
    List<Application> findAllByUserIdAndStatus(Long userId, ApplicationStatus status);
}
