package com.personalloan.module.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Component
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class RedisRateLimiter implements RateLimiter {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void checkLimit(String key, int maxRequests, long windowSeconds) {
        Instant now = Instant.now();
        double nowMs = now.toEpochMilli();
        double windowStartMs = nowMs - (windowSeconds * 1000);
        String value = UUID.randomUUID().toString(); // unique value to avoid collisions in Sorted Set

        try {
            // Using a Redis Sorted Set (ZSET) for sliding window rate limiting
            // 1. Remove elements older than the sliding window start
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStartMs);

            // 2. Count the number of requests in the current window
            Long count = redisTemplate.opsForZSet().zCard(key);

            if (count != null && count >= maxRequests) {
                log.warn("Redis Rate limit violation detected for key: {}", key);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, 
                        "Too many requests. Please try again after some time.");
            }

            // 3. Log the current request
            redisTemplate.opsForZSet().add(key, value, nowMs);
            
            // 4. Set TTL on the key to clean up inactive sets
            redisTemplate.expireAt(key, now.plusSeconds(windowSeconds));
            
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            // Log error and fall back to allowing the request to proceed so database issues do not crash the service
            log.error("Error executing rate limit check in Redis. Falling back to allow access.", ex);
        }
    }
}
