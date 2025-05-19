package masterit.masterit.entities;


import masterit.masterit.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    @NotBlank
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @NotBlank
    @Email
    private String email;

    @Column(name = "provider", nullable = false)
    @NotBlank
    private String provider;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "email_verified_at")
    private Date emailVerifiedAt;

}
