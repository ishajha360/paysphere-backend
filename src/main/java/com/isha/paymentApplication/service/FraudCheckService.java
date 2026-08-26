package com.isha.paymentApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FraudCheckService {

    private final StringRedisTemplate redisTemplate;

    private static final int CARD_LIMIT = 5;
    private static final int IP_LIMIT = 10;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    public boolean isBlocked(String cardHash, String ip) {
        long cardAttempts = incrementAndGet("attempts:card:" + cardHash);
        long ipAttempts = incrementAndGet("attempts:ip:" + ip);

        return cardAttempts > CARD_LIMIT || ipAttempts > IP_LIMIT;
    }

    private long incrementAndGet(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, WINDOW);
        }
        return count == null ? 0 : count;
    }
}
