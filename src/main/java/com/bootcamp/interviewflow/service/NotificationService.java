package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.User;

public interface NotificationService {


    void scheduleInterviewReminder(Application application, User user);

    NotificationStats getStats();

    void cancelNotificationsForApplication(Long applicationId);

    record NotificationStats(
            long pendingCount,
            long sentCount,
            long failedCount,
            long cancelledCount
    ) {}

}