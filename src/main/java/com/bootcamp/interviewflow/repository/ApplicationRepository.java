package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

}


