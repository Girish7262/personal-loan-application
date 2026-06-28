package com.personalloan.common.security;

import com.personalloan.module.auth.internal.service.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIp(request);

        try {
            // Apply targeted sliding window rate limits based on incoming requests URIs
            if (path.endsWith("/auth/login")) {
                rateLimiter.checkLimit("rate_limit:login:" + clientIp, 5, 60);
            } else if (path.endsWith("/auth/forgot-password")) {
                rateLimiter.checkLimit("rate_limit:forgot:" + clientIp, 3, 900);
            } else if (path.endsWith("/auth/register")) {
                rateLimiter.checkLimit("rate_limit:register:" + clientIp, 10, 3600);
            }
        } catch (ResponseStatusException ex) {
            log.warn("Rate limit violation blocked at filter level for client IP: {} on URI: {}", clientIp, path);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(String.format(
                    "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"%s\"}",
                    ex.getReason() != null ? ex.getReason() : "Too many requests. Please try again later."
            ));
            return; // Terminate filter chain and block execution
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
