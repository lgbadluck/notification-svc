package com.softuni.notification_svc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

// DTO = contract
@Data
@Builder
public class UpsertNotificationSettings {

    @NotNull
    private UUID userId;

    private boolean notificationEnabled;

    @NotNull
    private NotificationTypeRequest type;

    @NotBlank
    private String contactInfo;
}
