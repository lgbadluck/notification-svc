package com.softuni.notification_svc.repository;


import com.softuni.notification_svc.model.Notification;
import com.softuni.notification_svc.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByUserIdAndDeletedIsFalse(UUID userId);

    List<Notification> findAllByUserIdAndStatus(UUID userId, NotificationStatus status);
}

