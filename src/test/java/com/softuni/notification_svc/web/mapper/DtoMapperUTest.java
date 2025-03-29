package com.softuni.notification_svc.web.mapper;

import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.model.NotificationStatus;
import com.softuni.notification_svc.model.NotificationType;
import com.softuni.notification_svc.web.dto.NotificationResponse;
import com.softuni.notification_svc.web.dto.NotificationSettingsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenHappyPath_whenMappingNotificationSettingsToNotificationSettingsResponse() {

        // Given
        NotificationSettings notificationSettings = NotificationSettings.builder()
                .id(UUID.randomUUID())
                .type(NotificationType.EMAIL)
                .contactInfo("contact@email.com")
                .isEnabled(true)
                .userId(UUID.randomUUID())
                .build();

        // When
        NotificationSettingsResponse resultDto = DtoMapper.fromNotificationSettings(notificationSettings);

        // Then
        assertEquals(notificationSettings.getId(), resultDto.getId());
        assertEquals(notificationSettings.getType(), resultDto.getType());
        assertEquals(notificationSettings.getContactInfo(), resultDto.getContactInfo());
        assertEquals(notificationSettings.isEnabled(), resultDto.isEnabled());
        assertEquals(notificationSettings.getUserId(), resultDto.getUserId());
    }

    @Test
    void givenHappyPath_whenMappingNotificationToNotificationResponse() {

        // Given
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(NotificationStatus.SUCCEEDED)
                .type(NotificationType.EMAIL)
                .subject("Subject text")
                .body("Body text")
                .createdOn(LocalDateTime.now())
                .deleted(false)
                .build();

        // When
        NotificationResponse resultDto = DtoMapper.fromNotification(notification);

        // Then
        assertEquals(notification.getSubject(), resultDto.getSubject());
        assertEquals(notification.getStatus(), resultDto.getStatus());
        assertEquals(notification.getCreatedOn(), resultDto.getCreatedOn());
        assertEquals(notification.getType(), resultDto.getType());
    }
}
