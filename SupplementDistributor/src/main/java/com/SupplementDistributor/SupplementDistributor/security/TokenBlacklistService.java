package com.SupplementDistributor.SupplementDistributor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    // Prefijo para las keys en Redis — evita colisiones con otras keys
    private static final String BLACKLIST_PREFIX = "blacklist:";

    // Agrega el token a la blacklist con TTL igual al tiempo restante del token
    public void blacklistToken(String token, long expirationMillis) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(
                key,
                "invalidated",
                expirationMillis,
                TimeUnit.MILLISECONDS   // El token se borra solo cuando expira
        );
    }

    // Verifica si el token está en la blacklist
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}
