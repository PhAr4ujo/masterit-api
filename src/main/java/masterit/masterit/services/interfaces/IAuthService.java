package masterit.masterit.services.interfaces;

import jakarta.mail.MessagingException;
import masterit.masterit.dtos.input.RegisterDTO;
import masterit.masterit.dtos.output.UserDTO;

public interface IAuthService {
    public UserDTO register(RegisterDTO request) throws MessagingException;
    public String verifyAndLogin(String token);
}
