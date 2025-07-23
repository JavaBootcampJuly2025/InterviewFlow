package com.bootcamp.interviewflow.repository;

import com.bootcamp.interviewflow.model.Notification;
import com.bootcamp.interviewflow.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n FROM Notification n 
        WHERE n.status = 'PENDING' 
        AND n.scheduledTime <= :currentTime 
        ORDER BY n.scheduledTime ASC
        """)
    Page<Notification> findDueNotifications(
            @Param("currentTime") LocalDateTime currentTime,
            Pageable pageable
    );


    @Modifying
    @Query("UPDATE Notification n SET n.status = 'CANCELLED' WHERE n.applicationId = :applicationId AND n.status = 'PENDING'")
    void cancelPendingNotificationsByApplicationId(@Param("applicationId") Long applicationId);

    long countByStatus(NotificationStatus status);

    boolean existsByApplicationIdAndStatus(Long applicationId, NotificationStatus status);
}