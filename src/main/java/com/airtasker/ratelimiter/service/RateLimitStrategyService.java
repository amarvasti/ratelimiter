package com.airtasker.ratelimiter.service;

import com.airtasker.ratelimiter.model.RemainingQuota;

public interface RateLimitStrategyService {
    public abstract RemainingQuota getRemainingQuota(String apiKey);
}
