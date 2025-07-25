package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.User;

public interface NotificationService {
    void scheduleInterviewReminder(Application application, User user);
    void cancelNotificationsForApplication(Long applicationId);
}