package com.airtasker.ratelimiter.service.impl;

import com.airtasker.ratelimiter.service.RateLimitStrategyService;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractRateLimitStrategy implements RateLimitStrategyService {
    @Value("${rateLimiter.numOfAllowedRequests}")
    private int numOfAllowedRequests;

    @Value("${rateLimiter.durationInSeconds}")
    private int durationInSeconds;

    public int getNumOfAllowedRequests() {
        return numOfAllowedRequests;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }
}
