package com.softuni.notification_svc.web.dto;

import com.softuni.notification_svc.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationSettingsResponse {

    private UUID id;

    private UUID userId;

    private NotificationType type;

    private boolean enabled;

    private String contactInfo;
}
