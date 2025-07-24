package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.EmailRequest;
import com.bootcamp.interviewflow.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailReminderService {

    private static final Logger log = LoggerFactory.getLogger(EmailReminderService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Store scheduled reminder emails
    private final ConcurrentHashMap<String, EmailRequest> scheduledReminders = new ConcurrentHashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void scheduleInterviewReminder(Application application, String userEmail) {
        if (application.getInterviewDate() == null ||
                !Boolean.TRUE.equals(application.getEmailNotificationsEnabled())) {
            log.info("Skipping reminder scheduling - interview date null or notifications disabled for application {}",
                    application.getId());
            return;
        }

        // Calculate reminder time (2 hours before interview)
        LocalDateTime reminderTime = application.getInterviewDate().minusHours(2);

        // Don't schedule if reminder time is in the past
        if (reminderTime.isBefore(LocalDateTime.now())) {
            log.info("Skipping reminder scheduling - reminder time is in the past for application {}",
                    application.getId());
            return;
        }

        String scheduledTimeStr = reminderTime.format(formatter);
        String subject = String.format("Interview Reminder - %s at %s",
                application.getPosition(), application.getCompanyName());

        String message = createReminderMessage(application);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setSenderEmail(senderEmail);
        emailRequest.setRecipientEmail(userEmail);
        emailRequest.setSubject(subject);
        emailRequest.setMessage(message);
        emailRequest.setScheduledTime(scheduledTimeStr);
        emailRequest.setCompanyName(application.getCompanyName());
        emailRequest.setPosition(application.getPosition());
        emailRequest.setApplicationId(application.getId());

        // Use unique key combining application ID and reminder time
        String key = application.getId() + "_" + scheduledTimeStr;
        scheduledReminders.put(key, emailRequest);

        log.info("Interview reminder scheduled for application {} at {}",
                application.getId(), scheduledTimeStr);
    }

    public void cancelInterviewReminder(Long applicationId) {
        // Remove any scheduled reminders for this application
        scheduledReminders.entrySet().removeIf(entry -> {
            EmailRequest emailRequest = entry.getValue();
            return emailRequest.getApplicationId().equals(applicationId);
        });

        log.info("Cancelled any scheduled reminders for application {}", applicationId);
    }

    private String createReminderMessage(Application application) {
        return String.format("""
                Hello!
                
                This is a friendly reminder that you have an interview scheduled in 2 hours.
                
                Interview Details:
                • Company: %s
                • Position: %s
                • Interview Date: %s
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

    private void sendEmailNow(EmailRequest emailRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(emailRequest.getRecipientEmail());
            message.setText(emailRequest.getMessage());
            message.setSubject(emailRequest.getSubject());

            mailSender.send(message);
            log.info("Interview reminder sent to: {} for application {}",
                    emailRequest.getRecipientEmail(), emailRequest.getApplicationId());
        } catch (Exception e) {
            log.error("Failed to send interview reminder for application {}: {}",
                    emailRequest.getApplicationId(), e.getMessage());
            throw e;
        }
    }

    // Check every minute for scheduled reminders
    @Scheduled(fixedRate = 60000)
    public void checkScheduledReminders() {
        LocalDateTime now = LocalDateTime.now();
        String currentTime = now.format(formatter);

        scheduledReminders.entrySet().removeIf(entry -> {
            EmailRequest emailRequest = entry.getValue();
            String scheduledTime = emailRequest.getScheduledTime();

            // Check if it's time to send (current time >= scheduled time)
            if (scheduledTime.compareTo(currentTime) <= 0) {
                try {
                    sendEmailNow(emailRequest);
                    log.info("Scheduled interview reminder sent for application: {}",
                            emailRequest.getApplicationId());
                    return true; // Remove from scheduled list
                } catch (Exception e) {
                    log.error("Failed to send scheduled interview reminder for application {}: {}",
                            emailRequest.getApplicationId(), e.getMessage());
                    return false; // Keep in list to retry
                }
            }
            return false; // Keep in list
        });
    }
}