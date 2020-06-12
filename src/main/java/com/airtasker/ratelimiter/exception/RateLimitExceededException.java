package com.airtasker.ratelimiter.exception;

public class RateLimitExceededException extends RuntimeException {
    private int retryAfterInSeconds;

    public RateLimitExceededException(int numberOfSeconds) {
        super("Rate limit exceeded. Try again in " + numberOfSeconds + " seconds");
        this.retryAfterInSeconds = numberOfSeconds;
    }

    public int getRetryAfterInSeconds() {
        return this.retryAfterInSeconds;
    }
}
