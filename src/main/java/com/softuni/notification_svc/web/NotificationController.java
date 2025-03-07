package com.softuni.notification_svc.web;

import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.service.NotificationService;
import com.softuni.notification_svc.web.dto.NotificationRequest;
import com.softuni.notification_svc.web.dto.NotificationResponse;
import com.softuni.notification_svc.web.dto.NotificationSettingsResponse;
import com.softuni.notification_svc.web.dto.UpsertNotificationSettings;
import com.softuni.notification_svc.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> upsertNotificationSettings(@RequestBody UpsertNotificationSettings upsertNotificationSettings) {

        NotificationSettings notificationPreference = notificationService.upsertNotificationSettings(upsertNotificationSettings);

        NotificationSettingsResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> getUserNotificationSettings(@RequestParam(name = "userId") UUID userId) {

        NotificationSettings notificationPreference = notificationService.getPreferenceByUserId(userId);

        NotificationSettingsResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest notificationRequest) {

        // Entity
        Notification notification = notificationService.sendNotification(notificationRequest);

        // DTO
        NotificationResponse response = DtoMapper.fromNotification(notification);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotificationHistory(@RequestParam(name = "userId") UUID userId) {

        List<NotificationResponse> notificationHistory = notificationService.getNotificationHistory(userId).stream().map(DtoMapper::fromNotification).toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationHistory);
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingsResponse> changeNotificationPreference(@RequestParam(name = "userId") UUID userId, @RequestParam(name = "enabled") boolean enabled) {

        NotificationSettings notificationSettings = notificationService.changeNotificationSettings(userId, enabled);

        NotificationSettingsResponse responseDto = DtoMapper.fromNotificationPreference(notificationSettings);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    //  Endpoint: GET /api/v1/notifications/test  = "Hello, unknown user!"
    @GetMapping("/test")
    public ResponseEntity<String> getHelloWorld(@RequestParam(name = "name") String name) {

        return ResponseEntity.ok("Hello, " + name + " user!");
    }
}
