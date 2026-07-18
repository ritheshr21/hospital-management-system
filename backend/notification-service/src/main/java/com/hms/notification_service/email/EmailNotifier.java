package com.hms.notification_service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.hms.notification_service.entity.Notification;

import lombok.RequiredArgsConstructor;

/**
 * Email delivery is optional. It only sends when
 * {@code hms.notifications.email.enabled=true} AND a JavaMailSender is present
 * (i.e. spring.mail.* is configured). Otherwise it just logs, so the system
 * works out of the box with no SMTP credentials.
 */
@Component
@RequiredArgsConstructor
public class EmailNotifier {

    private static final Logger log = LoggerFactory.getLogger(EmailNotifier.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${hms.notifications.email.enabled}")
    private boolean emailEnabled;

    @Value("${hms.notifications.email.from}")
    private String from;

    public void send(Notification notification) {
        if (!emailEnabled) {
            log.debug("Email disabled; notification '{}' stored in-app only", notification.getTitle());
            return;
        }

        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.warn("Email enabled but no mail sender configured (set spring.mail.*); skipping email");
            return;
        }
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().isBlank()) {
            log.debug("No recipient email on notification '{}'; skipping email", notification.getTitle());
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(notification.getRecipientEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());
            sender.send(message);
            log.info("Emailed '{}' to {}", notification.getTitle(), notification.getRecipientEmail());
        } catch (Exception e) {
            // Never let email failure break the event pipeline.
            log.error("Failed to send email for notification {}", notification.getId(), e);
        }
    }
}
