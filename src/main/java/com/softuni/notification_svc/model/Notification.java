package com.softuni.notification_svc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(nullable = false)
        private String subject;

        @Column(nullable = false)
        private String body;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private NotificationStatus status;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private NotificationType type;

        private UUID userId;

        private boolean deleted;

        @CreationTimestamp
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdOn;

        @UpdateTimestamp
        private LocalDateTime updatedOn;
}

