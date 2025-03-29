package com.softuni.notification_svc.web.mapper;

import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.model.NotificationType;
import com.softuni.notification_svc.web.dto.NotificationResponse;
import com.softuni.notification_svc.web.dto.NotificationSettingsResponse;
import com.softuni.notification_svc.web.dto.NotificationTypeRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    // Mapping logic: прехвърляме един тип данни към друг
    public static NotificationType fromNotificationTypeRequest(NotificationTypeRequest dto) {

        return switch (dto) {
            case EMAIL -> NotificationType.EMAIL;
        };
    }

    // Build dto from entity
    public static NotificationSettingsResponse fromNotificationSettings(NotificationSettings entity) {

        return NotificationSettingsResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .contactInfo(entity.getContactInfo())
                .enabled(entity.isEnabled())
                .userId(entity.getUserId())
                .build();
    }

    public static NotificationResponse fromNotification(Notification entity) {

        // DTO building!
        return NotificationResponse.builder()
                .subject(entity.getSubject())
                .status(entity.getStatus())
                .createdOn(entity.getCreatedOn())
                .type(entity.getType())
                .build();
    }}
