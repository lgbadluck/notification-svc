package com.softuni.notification_svc;

import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.repository.NotificationSettingsRepository;
import com.softuni.notification_svc.service.NotificationService;
import com.softuni.notification_svc.web.dto.NotificationTypeRequest;
import com.softuni.notification_svc.web.dto.UpsertNotificationSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CreateNewPreferenceITest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationSettingsRepository settingsRepository;

    @Test
    void createNewNotificationPreference_happyPath() {

        // Given
        UUID userId = UUID.randomUUID();
        UpsertNotificationSettings upsertNotificationSettings = UpsertNotificationSettings.builder()
                .userId(userId)
                .type(NotificationTypeRequest.EMAIL)
                .notificationEnabled(true)
                .contactInfo("test@email.com")
                .build();

        // When
        notificationService.upsertNotificationSettings(upsertNotificationSettings);

        // Then
        List<NotificationSettings> notificationSettingsList = settingsRepository.findAll();
        assertThat(notificationSettingsList).hasSize(1);
        NotificationSettings preference = notificationSettingsList.get(0);
        assertEquals(userId, preference.getUserId());
    }
}