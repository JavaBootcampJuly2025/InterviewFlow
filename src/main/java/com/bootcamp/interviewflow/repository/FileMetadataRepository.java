package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
    Optional<FileMetadata> findByIdAndUserId(UUID id, Long userId);
    List<FileMetadata> findAllByUserId(Long userId);
}
