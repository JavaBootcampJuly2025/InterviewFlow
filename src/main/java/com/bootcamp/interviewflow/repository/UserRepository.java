package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
