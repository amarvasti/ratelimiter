package com.airtasker.ratelimiter.controller;

import com.airtasker.ratelimiter.exception.RateLimitExceededException;
import com.airtasker.ratelimiter.model.RemainingQuota;
import com.airtasker.ratelimiter.service.RateLimitStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {
    public static final String ALLOWED = "Allowed";

    @Autowired
    @Qualifier("slidingWindowRateLimitStrategy")
    RateLimitStrategyService rateLimiterService;

    @GetMapping("/ratelimit")
    public ResponseEntity<String> rateLimit(@RequestHeader(value = "X-api-key") String apiKey) {
        RemainingQuota remainingQuota = rateLimiterService.getRemainingQuota(apiKey);
        if (remainingQuota.getRemainingAttempts() > 0) {
            return ResponseEntity.ok()
                    .header("X-Rate-Limit-Remaining", String.valueOf(remainingQuota.getRemainingAttempts()))
                    .body(ALLOWED);
        } else {
            throw new RateLimitExceededException(remainingQuota.getRemainingSeconds());
        }
    }
}
