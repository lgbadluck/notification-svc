package com.softuni.notification_svc.service;

import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.model.NotificationStatus;
import com.softuni.notification_svc.repository.NotificationRepository;
import com.softuni.notification_svc.repository.NotificationSettingsRepository;
import com.softuni.notification_svc.web.dto.NotificationRequest;
import com.softuni.notification_svc.web.dto.UpsertNotificationSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.softuni.notification_svc.web.TestBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceUTest {

    @Mock
    private NotificationSettingsRepository settingsRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void givenNotExistingNotificationSettings_whenChangeNotificationSetting_thenExpectException(){

        // Given
        UUID userId = UUID.randomUUID();
        boolean isNotificationEnabled = true;
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NullPointerException.class, () -> notificationService.changeNotificationSettings(userId, isNotificationEnabled));
    }

    @Test
    void givenExistingNotificationSettings_whenChangeNotificationSetting_thenExpectEnabledToBeChanged(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        notificationSettings.setEnabled(false);
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(notificationSettings));

        // When
        notificationService.changeNotificationSettings(userId, true);

        // Then
        assertTrue(notificationSettings.isEnabled());
        verify(settingsRepository, times(1)).save(notificationSettings);
    }


    @Test
    void givenExistingNotificationHistory_whenClearNotifications_happyPath(){

        // Given
        UUID userId = UUID.randomUUID();
        Notification notification1 = aRandomNotification(userId);
        Notification notification2 = aRandomNotification(userId);
        notification1.setDeleted(true);
        notification2.setDeleted(true);
        List<Notification> notificationList = List.of(notification1, notification2);
        when(notificationRepository.findAllByUserIdAndDeletedIsFalse(userId)).thenReturn(notificationList);

        // When
        notificationService.clearNotifications(userId);

        // Then
        assertTrue(notification1.isDeleted());
        assertTrue(notification2.isDeleted());
        verify(notificationRepository, times(1)).findAllByUserIdAndDeletedIsFalse(userId);
    }

    @Test
    void givenExistingNotificationSettingsEnabledTrue_whenRetryFailedNotifications_happyPath(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        Notification notification1 = aRandomNotification(userId);
        Notification notification2 = aRandomNotification(userId);
        notification1.setStatus(NotificationStatus.FAILED);
        notification2.setStatus(NotificationStatus.FAILED);
        List<Notification> failNotificationList = List.of(notification1, notification2);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(notificationSettings));
        when(notificationRepository.findAllByUserIdAndStatus(any(), any(NotificationStatus.class))).thenReturn(failNotificationList);

        // When
        notificationService.retryFailedNotifications(userId);

        // Then
        assertEquals(NotificationStatus.SUCCEEDED, notification1.getStatus());
        assertEquals(NotificationStatus.SUCCEEDED, notification2.getStatus());
        verify(notificationRepository, times(2)).save(any());
    }

    @Test
    void givenExistingNotificationSettingsEnabledTrueMailSenderError_whenRetryFailedNotifications_thenLoggedError(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        Notification notification1 = aRandomNotification(userId);
        Notification notification2 = aRandomNotification(userId);
        notification1.setStatus(NotificationStatus.FAILED);
        notification2.setStatus(NotificationStatus.FAILED);
        List<Notification> failNotificationList = List.of(notification1, notification2);

        when(settingsRepository.findByUserId(userId))
                .thenReturn(Optional.of(notificationSettings));
        when(notificationRepository.findAllByUserIdAndStatus(any(), any(NotificationStatus.class))).thenReturn(failNotificationList);
        doThrow(new MailException("Email sending failed") {})
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        // Capture System.out logs
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When
        notificationService.retryFailedNotifications(userId);

        // Then
        assertTrue(outContent.toString().contains("There was an issue sending an email"),"Expected warning log was not printed.");
        assertEquals(NotificationStatus.FAILED, notification1.getStatus());
        assertEquals(NotificationStatus.FAILED, notification2.getStatus());
        verify(notificationRepository, times(2)).save(any());
    }

    @Test
    void givenExistingNotificationSettingsEnabledFalse_whenRetryFailedNotifications_thenExpectException(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        notificationSettings.setEnabled(false);
        Notification notification1 = aRandomNotification(userId);
        Notification notification2 = aRandomNotification(userId);
        notification1.setStatus(NotificationStatus.FAILED);
        notification2.setStatus(NotificationStatus.FAILED);
        List<Notification> failNotificationList = List.of(notification1, notification2);

        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(notificationSettings));

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> notificationService.retryFailedNotifications(userId));
        assertEquals(NotificationStatus.FAILED, notification1.getStatus());
        assertEquals(NotificationStatus.FAILED, notification2.getStatus());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void givenExistingNotificationSettingsEnabledTrue_whenSendNotification_happyPath(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        NotificationRequest notificationRequest = aRandomNotificationRequest(userId);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(notificationSettings));

        // When
        notificationService.sendNotification(notificationRequest);

        // Then
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void givenExistingNotificationSettingsEnabledTrueMailSenderError_whenSendNotification_thenLoggedError(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        NotificationRequest notificationRequest = aRandomNotificationRequest(userId);

        when(settingsRepository.findByUserId(userId))
                .thenReturn(Optional.of(notificationSettings));
        doThrow(new MailException("Email sending failed") {})
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        // Capture System.out logs
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When
        notificationService.sendNotification(notificationRequest);

        // Then
        assertTrue(outContent.toString().contains("There was an issue sending an email"),"Expected warning log was not printed.");
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    void givenExistingNotificationSettingsEnabledFalse_whenSendNotification_thenExpectException(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        notificationSettings.setEnabled(false);
        NotificationRequest notificationRequest = aRandomNotificationRequest(userId);

        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(notificationSettings));

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(notificationRequest));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void givenExistingNotificationSettings_whenUpsertNotificationSettings_happyPathInsertToDB(){

        // Given
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);
        UpsertNotificationSettings upsertNotificationSettings = aRandomUpsertNotificationSettings(userId);

        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.of(notificationSettings));

        // When
        notificationService.upsertNotificationSettings(upsertNotificationSettings);

        // Then
        verify(settingsRepository, times(1)).save(any());
    }

    @Test
    void givenNonExistingNotificationSettings_whenUpsertNotificationSettings_happyPathNewSaveToDB(){

        // Given
        UUID userId = UUID.randomUUID();
        UpsertNotificationSettings upsertNotificationSettings = aRandomUpsertNotificationSettings(userId);

        when(settingsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        notificationService.upsertNotificationSettings(upsertNotificationSettings);

        // Then
        verify(settingsRepository, times(1)).save(any());
    }
}
