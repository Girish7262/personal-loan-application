package com.personalloan.module.auth.internal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!prod")
@Slf4j
public class InMemoryRateLimiter implements RateLimiter {

    private final ConcurrentHashMap<String, List<Instant>> requestLogs = new ConcurrentHashMap<>();

    @Override
    public void checkLimit(String key, int maxRequests, long windowSeconds) {
        Instant now = Instant.now();
        Instant windowStart = now.minusSeconds(windowSeconds);

        requestLogs.compute(key, (k, logs) -> {
            if (logs == null) {
                logs = new ArrayList<>();
            }

            // Remove timestamps outside of current sliding window
            logs.removeIf(timestamp -> timestamp.isBefore(windowStart));

            if (logs.size() >= maxRequests) {
                log.warn("In-Memory Rate limit violation detected for key: {}", key);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, 
                        "Too many requests. Please try again after some time.");
            }

            logs.add(now);
            return logs;
        });
    }
}
