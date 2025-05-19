package masterit.masterit.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import masterit.masterit.dtos.input.RegisterDTO;
import masterit.masterit.dtos.output.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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


}
