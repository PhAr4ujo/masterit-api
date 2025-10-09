package masterit.masterit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "roadmaps")
@Getter
@Setter
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = false, updatable = true)
    @NotBlank
    private String title;

    @Column(nullable = false, unique = false, updatable = true)
    @NotBlank
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
}
