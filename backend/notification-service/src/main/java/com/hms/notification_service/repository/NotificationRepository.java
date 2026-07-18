package com.hms.notification_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.notification_service.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findByReadFlagFalseOrderByCreatedAtDesc();

    long countByReadFlagFalse();

    boolean existsByReferenceIdAndType(UUID referenceId, com.hms.notification_service.entity.NotificationType type);
}
