package masterit.masterit.controllers;

import lombok.AllArgsConstructor;
import masterit.masterit.repositories.UserRepository;
import masterit.masterit.services.interfaces.IAuthService;
import masterit.masterit.services.interfaces.IJwtService;
import masterit.masterit.services.interfaces.ITokenBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ITokenBlackListService blacklistTokenService;

    @GetMapping("/blacklist")
    public String testBlacklist() {
        String token = "token123";
        blacklistTokenService.blacklistToken(token, 60000); // 1 min
        return "Token blacklisted? " + blacklistTokenService.isTokenBlacklisted(token);
    }
}
