package com.softuni.notification_svc.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationSettings;
import com.softuni.notification_svc.service.NotificationService;
import com.softuni.notification_svc.web.dto.NotificationRequest;
import com.softuni.notification_svc.web.dto.NotificationResponse;
import com.softuni.notification_svc.web.dto.UpsertNotificationSettings;
import com.softuni.notification_svc.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static com.softuni.notification_svc.web.TestBuilder.*;
import static com.softuni.notification_svc.web.TestBuilder.aRandomNotification;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postUpsertNotificationSettings_shouldReturnStatusCreated() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();
        UpsertNotificationSettings upsertNotificationSettings = aRandomUpsertNotificationSettings(userId);
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);

        // Serialize BudgetRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(upsertNotificationSettings);
        String expectedJson = objectMapper.writeValueAsString(DtoMapper.fromNotificationSettings(notificationSettings));

        MockHttpServletRequestBuilder request = post("/api/v1/notifications/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson);

        when(notificationService.upsertNotificationSettings(any())).thenReturn(notificationSettings);

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(notificationService, times(1)).upsertNotificationSettings(any());
    }


    @Test
    void getUserNotificationSettings_shouldReturnStatusOK() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();
        UpsertNotificationSettings upsertNotificationSettings = aRandomUpsertNotificationSettings(userId);
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);

        // Serialize BudgetRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBodyJson = objectMapper.writeValueAsString(upsertNotificationSettings);
        String expectedJson = objectMapper.writeValueAsString(DtoMapper.fromNotificationSettings(notificationSettings));

        MockHttpServletRequestBuilder request = get("/api/v1/notifications/settings")
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson);

        when(notificationService.getPreferenceByUserId(any())).thenReturn(notificationSettings);

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(notificationService, times(1)).getPreferenceByUserId(any());
    }

    @Test
    void postSendNotification_shouldReturnStatusCreated() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();
        NotificationRequest notificationRequest = aRandomNotificationRequest(userId);
        Notification notification = aRandomNotification(userId);

        // Serialize BudgetRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String requestBodyJson = objectMapper.writeValueAsString(notificationRequest);
        String expectedJson = objectMapper.writeValueAsString(DtoMapper.fromNotification(notification));

        MockHttpServletRequestBuilder request = post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson);

        when(notificationService.sendNotification(any())).thenReturn(notification);

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(notificationService, times(1)).sendNotification(any());
    }

    @Test
    void getNotificationHistory_shouldReturnStatusOK() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();
        Notification notification1 = aRandomNotification(userId);
        Notification notification2 = aRandomNotification(userId);
        List<Notification> notificationList = List.of(notification1, notification2);

        // Serialize BudgetRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String expectedJson = objectMapper.writeValueAsString(
                notificationList.stream().map(DtoMapper::fromNotification).toList() );

        MockHttpServletRequestBuilder request = get("/api/v1/notifications")
                .param("userId", String.valueOf(userId));

        when(notificationService.getNotificationHistory(any())).thenReturn(notificationList);

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(notificationService, times(1)).getNotificationHistory(any());
    }

    @Test
    void putChangeNotificationPreference_shouldReturnStatusOK() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();
        NotificationSettings notificationSettings = aRandomNotificationSettings(userId);

        // Serialize BudgetRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder request = put("/api/v1/notifications/settings")
                .param("userId", String.valueOf(userId))
                .param("enabled", String.valueOf(!notificationSettings.isEnabled()));

        when(notificationService.changeNotificationSettings(any(), any(Boolean.class))).thenReturn(notificationSettings);

        // 2. Send Request
        notificationSettings.setEnabled(!notificationSettings.isEnabled());
        String expectedJson = objectMapper.writeValueAsString(DtoMapper.fromNotificationSettings(notificationSettings));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(notificationService, times(1)).changeNotificationSettings(any(), any(Boolean.class));
    }


    @Test
    void deleteClearNotificationHistory_shouldReturnStatusOK() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = delete("/api/v1/notifications")
                .param("userId", String.valueOf(userId));

        doNothing().when(notificationService).clearNotifications(any());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(notificationService, times(1)).clearNotifications(any());
    }

    @Test
    void putRetryFailedNotifications_shouldReturnStatusOK() throws Exception {

        // 1. Build Request
        UUID userId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = put("/api/v1/notifications")
                .param("userId", String.valueOf(userId));

        doNothing().when(notificationService).retryFailedNotifications(any());

        // 2. Send Request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(notificationService, times(1)).retryFailedNotifications(any());
    }
}
