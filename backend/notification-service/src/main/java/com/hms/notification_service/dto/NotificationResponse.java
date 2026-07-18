package com.hms.notification_service.dto;

import java.time.Instant;
import java.util.UUID;

import com.hms.notification_service.entity.NotificationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private NotificationType type;
    private UUID recipientId;
    private String recipientName;
    private String title;
    private String message;
    private UUID referenceId;
    private boolean read;
    private Instant createdAt;
}
