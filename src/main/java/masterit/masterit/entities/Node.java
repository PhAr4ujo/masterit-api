package masterit.masterit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "nodes")
@Getter
@Setter
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "position_x", nullable = false)
    private float positionX;

    @Column(name = "position_y", nullable = false)
    private float positionY;

    @Column(nullable = false, unique = false, updatable = true)
    @NotBlank
    private String label;

    @Column(nullable = false, unique = false, updatable = true)
    @NotBlank
    private String description;

    @Column(nullable = false, unique = false, updatable = true)
    @NotBlank
    private String status;

    @Column(nullable = false, unique = false, updatable = true)
    @NotBlank
    private String priority;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    @NotNull
    private Roadmap roadmap;
}
