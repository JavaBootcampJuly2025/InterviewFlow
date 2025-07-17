package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByApplication_Id(Long applicationId);
}
