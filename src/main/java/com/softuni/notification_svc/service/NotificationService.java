package com.softuni.notification_svc.service;

import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.model.NotificationStatus;
import com.softuni.notification_svc.model.NotificationType;
import com.softuni.notification_svc.repository.NotificationRepository;
import com.softuni.notification_svc.repository.NotificationSettingsRepository;
import com.softuni.notification_svc.web.dto.NotificationRequest;
import com.softuni.notification_svc.web.dto.UpsertNotificationSettings;
import com.softuni.notification_svc.web.mapper.DtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationSettingsRepository settingsRepository;
    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;

    @Autowired
    public NotificationService(NotificationSettingsRepository settingsRepository,
                               NotificationRepository notificationRepository,
                               MailSender mailSender) {
        this.settingsRepository = settingsRepository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    public NotificationSettings upsertNotificationSettings(UpsertNotificationSettings dto) {

        // upsert

        // 1. try to find if such exist in the database
        Optional<NotificationSettings> userNotificationPreferenceOptional = settingsRepository.findByUserId(dto.getUserId());

        // 2. if exists - just update it
        if (userNotificationPreferenceOptional.isPresent()) {
            NotificationSettings preference = userNotificationPreferenceOptional.get();
            preference.setContactInfo(dto.getContactInfo());
            preference.setEnabled(dto.isNotificationEnabled());
            preference.setType(DtoMapper.fromNotificationTypeRequest(dto.getType()));
            preference.setUpdatedOn(LocalDateTime.now());
            return settingsRepository.save(preference);
        }

        // Here I build a new entity object!
        // 3. if does not exist - just create new one
        NotificationSettings notificationPreference = NotificationSettings.builder()
                .userId(dto.getUserId())
                .type(DtoMapper.fromNotificationTypeRequest(dto.getType()))
                .isEnabled(dto.isNotificationEnabled())
                .contactInfo(dto.getContactInfo())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return settingsRepository.save(notificationPreference);
    }

    public NotificationSettings getPreferenceByUserId(UUID userId) {

        return settingsRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("Notification preference for user id %s was not found.".formatted(userId)));
    }

    public Notification sendNotification(NotificationRequest notificationRequest) {

        UUID userId = notificationRequest.getUserId();
        NotificationSettings userPreference = getPreferenceByUserId(userId);

        if (!userPreference.isEnabled()) {
            throw new IllegalArgumentException("User with id %s does not allow to receive notifications.".formatted(userId));
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userPreference.getContactInfo());
        message.setSubject(notificationRequest.getSubject());
        message.setText(notificationRequest.getBody());

        // Entity building
        Notification notification = Notification.builder()
                .subject(notificationRequest.getSubject())
                .body(notificationRequest.getBody())
                .createdOn(LocalDateTime.now())
                .userId(userId)
                .deleted(false)
                .type(NotificationType.EMAIL)
                .build();

        try {
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SUCCEEDED);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.warn("There was an issue sending an email to %s due to %s.".formatted(userPreference.getContactInfo(), e.getMessage()));
        }

        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationHistory(UUID userId) {

        return notificationRepository.findAllByUserIdAndDeletedIsFalse(userId);
    }

    public NotificationSettings changeNotificationSettings(UUID userId, boolean enabled) {

        NotificationSettings notificationSettings = getPreferenceByUserId(userId);
        notificationSettings.setEnabled(enabled);
        return settingsRepository.save(notificationSettings);
    }
}
