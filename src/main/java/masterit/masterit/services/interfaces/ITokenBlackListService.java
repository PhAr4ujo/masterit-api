package masterit.masterit.services.interfaces;

public interface ITokenBlackListService {
    public void blacklistToken(String token, long ttlSeconds);
    public boolean isTokenBlacklisted(String token);
}
