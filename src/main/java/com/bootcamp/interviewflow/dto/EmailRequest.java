package com.bootcamp.interviewflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String message;

    // For scheduling interview reminders
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String scheduledTime;

    // Additional fields for interview context
    private String companyName;
    private String position;
    private Long applicationId;

    public EmailRequest(String senderEmail, String recipientEmail, String subject, String message) {
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
    }
}