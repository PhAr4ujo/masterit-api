package masterit.masterit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotBlank
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @Column(nullable = false)
    @NotNull
    private Date expiryDate;
}
