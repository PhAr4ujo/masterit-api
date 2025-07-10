package masterit.masterit.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import masterit.masterit.dtos.input.RegisterDTO;
import masterit.masterit.dtos.input.LoginDTO;
import masterit.masterit.dtos.output.UserDTO;
import masterit.masterit.entities.EmailVerificationToken;
import masterit.masterit.entities.User;
import masterit.masterit.enums.Role;
import lombok.AllArgsConstructor;
import masterit.masterit.repositories.EmailVerificationTokenRepository;
import masterit.masterit.services.interfaces.IJwtService;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import masterit.masterit.repositories.UserRepository;
import masterit.masterit.services.interfaces.IAuthService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Primary
@Service
@AllArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSenderImpl mailSender;
    private final IJwtService jwtService;

    @Override
    @Transactional
    public UserDTO register(RegisterDTO request) throws MessagingException {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProvider(request.getProvider());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(new Date());
        user.setProvider(request.getProvider());
        user.setEmailVerifiedAt(null);

        var savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        Date expiryDate = Date.from(Instant.now().plus(Duration.ofMinutes(15)));

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(expiryDate);

        emailVerificationTokenRepository.save(verificationToken);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String html = """
            <!DOCTYPE html>
            <html lang="pt-BR">
              <head>
                <meta charset="UTF-8">
                <title>Verificação de Conta</title>
              </head>
              <body style="font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 30px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); padding: 30px;">
                  <h2 style="color: #343a40; text-align: center;">👋 Olá!</h2>
                  <p style="font-size: 16px; color: #495057;">Seja bem-vindo(a) à <strong>Master IT</strong>! 🎉</p>
                  <p style="font-size: 16px; color: #495057;">Para concluir seu cadastro e ativar sua conta, clique no botão abaixo:</p>
                  
                  <div style="text-align: center; margin: 30px 0;">
                    <a href="http://localhost:8080/auth/verify/%s"
                       style="display: inline-block; font-size: 18px; font-weight: bold; color: #fff; background: linear-gradient(135deg, #0d6efd, #6610f2); padding: 14px 30px; border-radius: 8px; text-decoration: none; box-shadow: 0 4px 12px rgba(13, 110, 253, 0.4); transition: background 0.3s ease;">
                      Verificar Conta
                    </a>
                  </div>
                  
                  <p style="font-size: 14px; color: #6c757d;">Este link é válido por apenas <strong>15 minutos</strong>.</p>
                  <p style="font-size: 14px; color: #6c757d;">Se você não solicitou este registro, pode ignorar este e-mail com segurança.</p>
                  
                  <p style="font-size: 14px; color: #adb5bd; text-align: center; margin-top: 40px;">Com carinho,<br><strong>Equipe Master IT 💻</strong></p>
                </div>
              </body>
            </html>
            """.formatted(verificationToken.getToken());

        helper.setFrom("masteritcorp@gmail.com");
        helper.setTo(savedUser.getEmail());
        helper.setSubject("Veify Your Account");
        helper.setText(html, true);
        mailSender.send(mimeMessage);

        return toDTO(savedUser);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    @Override
    @Transactional
    public String verifyAndLogin(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token."));

        if (verificationToken.getExpiryDate().before(new Date())) {
            throw new IllegalArgumentException("Verification token has expired.");
        }

        User user = verificationToken.getUser();

        if (user.getEmailVerifiedAt() != null) {
            throw new IllegalArgumentException("User is already verified.");
        }

        user.setEmailVerifiedAt(new Date());
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);

        return jwtService.generateToken(user);
    }

    @Override
    public String login(LoginDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (user.getEmailVerifiedAt() == null) {
            throw new IllegalArgumentException("Email has not been verified.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return jwtService.generateToken(user);
    }

}
