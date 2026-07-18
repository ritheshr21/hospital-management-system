package com.hms.notification_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hms.notification_service.dto.NotificationResponse;
import com.hms.notification_service.email.EmailNotifier;
import com.hms.notification_service.entity.Notification;
import com.hms.notification_service.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotifier emailNotifier;

    /** Stores the notification, then fans out to email if enabled. */
    public Notification record(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        emailNotifier.send(saved);
        return saved;
    }

    public List<NotificationResponse> list(boolean unreadOnly) {
        List<Notification> notifications = unreadOnly
                ? notificationRepository.findByReadFlagFalseOrderByCreatedAtDesc()
                : notificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream().map(this::toResponse).toList();
    }

    public long unreadCount() {
        return notificationRepository.countByReadFlagFalse();
    }

    public NotificationResponse markRead(UUID id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        n.setReadFlag(true);
        return toResponse(notificationRepository.save(n));
    }

    public void markAllRead() {
        List<Notification> unread = notificationRepository.findByReadFlagFalseOrderByCreatedAtDesc();
        unread.forEach(n -> n.setReadFlag(true));
        notificationRepository.saveAll(unread);
    }

    /** Guards against Kafka redelivery creating duplicate notifications. */
    public boolean alreadyRecorded(UUID referenceId, com.hms.notification_service.entity.NotificationType type) {
        return referenceId != null && notificationRepository.existsByReferenceIdAndType(referenceId, type);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .recipientId(n.getRecipientId())
                .recipientName(n.getRecipientName())
                .title(n.getTitle())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .read(n.isReadFlag())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
