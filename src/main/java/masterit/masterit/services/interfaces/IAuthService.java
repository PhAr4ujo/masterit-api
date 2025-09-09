package masterit.masterit.services.interfaces;

import jakarta.mail.MessagingException;
import masterit.masterit.dtos.input.LoginDTO;
import masterit.masterit.dtos.input.RegisterDTO;
import masterit.masterit.dtos.input.ResetPasswordDTO;
import masterit.masterit.dtos.output.UserDTO;

public interface IAuthService {
    public UserDTO register(RegisterDTO request) throws MessagingException;
    public String verifyAndLogin(String token);
    public String login(LoginDTO request);
    public String resetPasswordRequest(String email) throws MessagingException;
    public String resetPassword(ResetPasswordDTO data);
    public void logout(String header);
}
