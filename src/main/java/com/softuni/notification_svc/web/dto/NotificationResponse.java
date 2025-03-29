package com.softuni.notification_svc.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.softuni.notification_svc.model.NotificationStatus;
import com.softuni.notification_svc.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private String subject;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdOn;

    private NotificationStatus status;

    private NotificationType type;
}
