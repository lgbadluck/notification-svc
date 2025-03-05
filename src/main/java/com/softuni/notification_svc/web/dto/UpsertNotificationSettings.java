package com.softuni.notification_svc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

// DTO = contract
@Data
public class UpsertNotificationSettings {

    @NotNull
    private UUID userId;

    private boolean notificationEnabled;

    @NotNull
    private NotificationTypeRequest type;

    @NotBlank
    private String contactInfo;
}
