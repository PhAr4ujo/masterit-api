package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import masterit.masterit.services.interfaces.ITokenBlackListService;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Primary
public class TokenBlacklistService implements ITokenBlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public void blacklistToken(String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "true",
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
