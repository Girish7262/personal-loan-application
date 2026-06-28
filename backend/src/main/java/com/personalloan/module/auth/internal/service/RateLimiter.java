package com.personalloan.module.auth.internal.service;

public interface RateLimiter {
    /**
     * Checks if a request violates the sliding-window rate limits.
     *
     * @param key unique sliding-window key (e.g. including target IP and action type)
     * @param maxRequests maximum request count in the window
     * @param windowSeconds sliding-window duration in seconds
     * @throws org.springframework.web.server.ResponseStatusException (HTTP 429) if requests exceed the limit
     */
    void checkLimit(String key, int maxRequests, long windowSeconds);
}
