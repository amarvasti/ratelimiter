package com.airtasker.ratelimiter.model;

public class RemainingQuota {
    private int remainingAttempts;
    private int remainingSeconds;

    public RemainingQuota(int remainingAttempts, int remainingSeconds) {
        this.remainingAttempts = remainingAttempts;
        this.remainingSeconds = remainingSeconds;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }
}
