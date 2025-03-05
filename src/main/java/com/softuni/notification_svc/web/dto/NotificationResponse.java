package com.softuni.notification_svc.web.dto;

import com.softuni.notification_svc.model.NotificationStatus;
import com.softuni.notification_svc.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String subject;

    private LocalDateTime createdOn;

    private NotificationStatus status;

    private NotificationType type;
}
