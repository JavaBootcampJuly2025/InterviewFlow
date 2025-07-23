package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.Notification;
import com.bootcamp.interviewflow.model.NotificationStatus;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ApplicationRepository applicationRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.notification.batch-size}")
    private int batchSize;

    @Override
    public void scheduleInterviewReminder(Application application, User user) {
        if (application.getInterviewDate() == null ||
                !Boolean.TRUE.equals(application.getEmailNotificationsEnabled())) {
            log.debug("Skipping notification scheduling for application {}", application.getId());
            return;
        }

        LocalDateTime reminderTime = application.getInterviewDate().minusHours(2);

        if (reminderTime.isBefore(LocalDateTime.now())) {
            log.debug("Reminder time is in the past for application {}", application.getId());
            return;
        }

        // Check if notification already exists for this application
        if (notificationRepository.existsByApplicationIdAndStatus(application.getId(), NotificationStatus.PENDING)) {
            log.debug("Notification already exists for application {}", application.getId());
            // Cancel existing and create new one
            cancelNotificationsForApplication(application.getId());
        }

        // Create new notification
        Notification notification = new Notification(
                application.getId(),
                user.getEmail(),
                createSubject(application),
                createMessage(application),
                reminderTime
        );

        notificationRepository.save(notification);
        log.info("Scheduled interview reminder for application {} at {}",
                application.getId(), reminderTime);
    }

    @Override
    @Transactional
    public void cancelNotificationsForApplication(Long applicationId) {
        notificationRepository.cancelPendingNotificationsByApplicationId(applicationId);
        log.debug("Cancelled pending notifications for application {}", applicationId);
    }


    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void processScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Processing scheduled notifications at {}", now);

        int processedCount = 0;

        // Process in batches to handle large volumes
        Pageable pageable = PageRequest.of(0, batchSize);
        Page<Notification> dueNotifications;

        do {
            dueNotifications = notificationRepository.findDueNotifications(now, pageable);

            for (Notification notification : dueNotifications.getContent()) {
                processSingleNotification(notification);
                processedCount++;
            }

            // Move to next batch
            pageable = pageable.next();

        } while (dueNotifications.hasNext());

        if (processedCount > 0) {
            log.info("Processed {} notifications", processedCount);
        }
    }

    private void processSingleNotification(Notification notification) {
        try {
            // Fetch fresh application data to ensure it still exists
            Optional<Application> applicationOpt = applicationRepository.findById(notification.getApplicationId());

            if (applicationOpt.isEmpty()) {
                notification.setStatus(NotificationStatus.CANCELLED);
                notificationRepository.save(notification);
                log.warn("Application {} no longer exists, cancelled notification {}",
                        notification.getApplicationId(), notification.getId());
                return;
            }

            Application application = applicationOpt.get();

            // Double-check if notifications are still enabled
            if (!Boolean.TRUE.equals(application.getEmailNotificationsEnabled())) {
                notification.setStatus(NotificationStatus.CANCELLED);
                notificationRepository.save(notification);
                log.info("Notifications disabled for application {}, cancelled notification {}",
                        application.getId(), notification.getId());
                return;
            }

            sendEmail(notification);

            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);

            log.info("Successfully sent notification {} for application {}",
                    notification.getId(), notification.getApplicationId());

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notificationRepository.save(notification);

            log.error("Failed to send notification {} for application {}: {}",
                    notification.getId(), notification.getApplicationId(), e.getMessage());
        }
    }

    private void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(notification.getRecipientEmail());
        message.setSubject(notification.getSubject());
        message.setText(notification.getMessage());

        mailSender.send(message);
    }

    private String createSubject(Application application) {
        return String.format("Interview Reminder - %s at %s",
                application.getPosition(), application.getCompanyName());
    }

    private String createMessage(Application application) {
        return String.format("""
            Hello!
            
            This is a reminder that you have an interview in 2 hours.
            
            Interview Details:
            • Company: %s
            • Position: %s
            • Date & Time: %s
            %s
            
            Good luck with your interview!
            
            Best regards,
            InterviewFlow Team
            """,
                application.getCompanyName(),
                application.getPosition(),
                application.getInterviewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                application.getCompanyLink() != null ? "• Company Website: " + application.getCompanyLink() : ""
        );
    }

    @Override
    public NotificationStats getStats() {
        return new NotificationStats(
                notificationRepository.countByStatus(NotificationStatus.PENDING),
                notificationRepository.countByStatus(NotificationStatus.SENT),
                notificationRepository.countByStatus(NotificationStatus.FAILED),
                notificationRepository.countByStatus(NotificationStatus.CANCELLED)
        );
    }
}