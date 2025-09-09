package masterit.masterit.services.interfaces;

import masterit.masterit.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface IJwtService {
    public String generateToken(User user);
    public boolean isTokenValid(String token, UserDetails userDetails);
    public String extractUsername(String token);
    public Date getExpirationDateFromToken(String token);

}
