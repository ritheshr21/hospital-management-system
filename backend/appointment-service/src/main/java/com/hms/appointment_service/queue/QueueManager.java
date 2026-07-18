package com.hms.appointment_service.queue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Redis-backed priority queue for the live waiting list.
 *
 * Appointments are held in a sorted set (ZSET) scored so that a LOWER score is
 * served first. The score encodes urgency in the high digits and arrival time in
 * the low digits, so a CRITICAL (urgency 5) case always jumps ahead of routine
 * ones, and within the same urgency it is first-come-first-served.
 */
@Component
@RequiredArgsConstructor
public class QueueManager {

    private static final String QUEUE_KEY = "hms:queue";
    private static final String TOKEN_KEY_PREFIX = "hms:token:";

    // Urgency band is weighted far above the arrival timestamp so urgency dominates.
    private static final double URGENCY_BAND = 1_000_000_000_000d;

    private final StringRedisTemplate redis;

    public void enqueue(UUID appointmentId, int urgency) {
        redis.opsForZSet().add(QUEUE_KEY, appointmentId.toString(), score(urgency));
    }

    public void remove(UUID appointmentId) {
        redis.opsForZSet().remove(QUEUE_KEY, appointmentId.toString());
    }

    /** Appointment ids in served order (highest priority first). */
    public List<UUID> orderedIds() {
        Set<String> ids = redis.opsForZSet().range(QUEUE_KEY, 0, -1);
        List<UUID> result = new ArrayList<>();
        if (ids != null) {
            ids.forEach(id -> result.add(UUID.fromString(id)));
        }
        return result;
    }

    public long size() {
        Long n = redis.opsForZSet().zCard(QUEUE_KEY);
        return n == null ? 0 : n;
    }

    /** Daily incrementing token number, reset each day via TTL. */
    public int nextToken() {
        String key = TOKEN_KEY_PREFIX + LocalDate.now();
        Long value = redis.opsForValue().increment(key);
        if (value != null && value == 1L) {
            redis.expire(key, Duration.ofDays(2));
        }
        return value == null ? 0 : value.intValue();
    }

    private double score(int urgency) {
        // (6 - urgency): urgency 5 -> band 1 (front), urgency 1 -> band 5 (back).
        return (6 - urgency) * URGENCY_BAND + Instant.now().getEpochSecond();
    }
}
