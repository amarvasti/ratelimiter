package com.airtasker.ratelimiter.service.impl;

import com.airtasker.ratelimiter.model.RemainingQuota;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * This class implements the rate limit strategy.
 * It uses the sliding window algorithm using a Redis Server sorted set.
 */

@Component
public class SlidingWindowRateLimitStrategy extends AbstractRateLimitStrategy {

    Logger logger = LoggerFactory.getLogger(SlidingWindowRateLimitStrategy.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private ZSetOperations zSetOperations;

    @PostConstruct
    public void init() {
        redisTemplate.setEnableTransactionSupport(true);
        zSetOperations = redisTemplate.opsForZSet();
    }

    @Override
    public RemainingQuota getRemainingQuota(String apiKey) {
        long now = System.currentTimeMillis();

        logger.info("User with API Key: {} has sent {} requests so far.", apiKey, zSetOperations.size(apiKey));
        long numOfOldRequests = zSetOperations.count(apiKey, 0, now - (this.getDurationInSeconds() * 1000));
        logger.info("{} requests were sent earlier than an hour ago and can be removed.", numOfOldRequests);

        /** Start the transaction */
        redisTemplate.multi();
        /** Remove the timestamps (requests from this API Key) that are older than an hour */
        zSetOperations.removeRangeByScore(apiKey, 0, now - (this.getDurationInSeconds() * 1000));
        /** Add the new timestamp (for the request that was just received) */
        zSetOperations.add(apiKey, now, now);
        /** Get the total number of remaining timestamps (request from this API Key) */
        zSetOperations.rangeByScore(apiKey, now - (this.getDurationInSeconds() * 1000), now);
        List<Object> result = redisTemplate.exec();

        /** rangeByScore command (3rd command in the transaction above) returns a set of timestamps within the last hour. */
        Set<Long> timestamps = (Set<Long>) result.get(2);

        int remainingAttempts = Math.max(0, this.getNumOfAllowedRequests() - timestamps.size());
        long firstRequestTimestamp = timestamps.iterator().next();
        long nowTimestamp = System.currentTimeMillis();
        int secondsPassedSinceFirstRequest = (int) (nowTimestamp - firstRequestTimestamp) / 1000;
        int remainingSeconds = this.getDurationInSeconds() - secondsPassedSinceFirstRequest;

        return new RemainingQuota(remainingAttempts, remainingSeconds);
    }
}
