package masterit.masterit.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import masterit.masterit.dtos.input.RegisterDTO;
import masterit.masterit.dtos.input.LoginDTO;
import masterit.masterit.dtos.output.UserDTO;
import masterit.masterit.entities.User;
import masterit.masterit.repositories.UserRepository;
import masterit.masterit.services.interfaces.IJwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import masterit.masterit.services.interfaces.IAuthService;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;
    private final JavaMailSender mailSender;
    private final IJwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        try {
            UserDTO userDTO = authService.register(data);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "User registered successfully. Please verify your email."
                    ));
        } catch (Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "An unexpected error occurred.",
                        "details", e.getMessage()
                ));
        }
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verify(@PathVariable String token) {
        try {
            String jwt = authService.verifyAndLogin(token);
            return ResponseEntity.ok(Map.of(
                    "message", "Email verified successfully!",
                    "token", jwt
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred.", "details", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserFromToken(@RequestHeader("Authorization") String authHeader) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO data) {
        try {
            String jwt = authService.login(data);

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful.",
                    "token", jwt
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred.", "details", e.getMessage()));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        // TODO blacklist
        return ResponseEntity.ok().build();
    }
}
