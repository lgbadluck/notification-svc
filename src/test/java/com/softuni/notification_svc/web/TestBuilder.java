package com.softuni.notification_svc.web;

import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.model.NotificationStatus;
import com.softuni.notification_svc.model.NotificationType;
import com.softuni.notification_svc.web.dto.NotificationRequest;
import com.softuni.notification_svc.web.dto.NotificationTypeRequest;
import com.softuni.notification_svc.web.dto.UpsertNotificationSettings;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static NotificationSettings aRandomNotificationSettings(UUID userID) {

        return NotificationSettings.builder()
                .id(UUID.randomUUID())
                .userId(userID)
                .isEnabled(true)
                .contactInfo("test@email.com")
                .type(NotificationType.EMAIL)
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static Notification aRandomNotification(UUID userID) {

        return Notification.builder()
                .id(UUID.randomUUID())
                .userId(userID)
                .deleted(false)
                .subject("Subject text")
                .body("Body text")
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.SUCCEEDED)
                .updatedOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
    }


    public static NotificationRequest aRandomNotificationRequest(UUID userID) {

        return NotificationRequest.builder()
                .userId(userID)
                .subject("Subject text")
                .body("Body text")
                .build();
    }

    public static UpsertNotificationSettings aRandomUpsertNotificationSettings(UUID userID) {

        return UpsertNotificationSettings.builder()
                .userId(userID)
                .notificationEnabled(true)
                .type(NotificationTypeRequest.EMAIL)
                .contactInfo("test@email.com")
                .build();
    }
}
