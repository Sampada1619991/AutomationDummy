package com.library.borrowing.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * Rate limiter service using Bucket4j.
 * 
 * This implementation uses a simple in-memory store (ConcurrentHashMap) for the demo.
 * In production, this should be backed by Redis or Hazelcast to ensure limits hold across pods.
 * See Phase 4 Task A-2 for production configuration.
 * 
 * Rate limit: {rateLimit} requests per second per member
 */
@Slf4j
@Service
public class RateLimitService {
    
    private final Map<UUID, Bucket> cache = new ConcurrentHashMap<>();
    private final Integer rateLimit;
    
    @Value("${library.borrowing.rate-limit.rps:20}")
    private Integer ratePerSecond;

    public RateLimitService(@Value("${library.borrowing.rate-limit.rps:20}") Integer ratePerSecond) {
        this.rateLimit = ratePerSecond;
        this.ratePerSecond = ratePerSecond;
    }

    /**
     * Check if a member has reached their rate limit.
     * 
     * @param memberId the member ID
     * @return true if the request is allowed (within limit), false if rate limit exceeded
     */
    public boolean isAllowed(UUID memberId) {
        Bucket bucket = cache.computeIfAbsent(memberId, k -> createNewBucket());
        boolean allowed = bucket.tryConsume(1);
        
        if (!allowed) {
            log.warn("Rate limit exceeded for member: {}", memberId);
        }
        
        return allowed;
    }

    /**
     * Create a new rate-limit bucket for a member.
     * Allows {ratePerSecond} tokens per second with a burst capacity of 2× the rate.
     * 
     * @return a new Bucket with the configured rate limit
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(ratePerSecond, Refill.intervally(ratePerSecond, Duration.ofSeconds(1)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }
}

