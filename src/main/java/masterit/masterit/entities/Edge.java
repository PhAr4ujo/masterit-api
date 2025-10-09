package masterit.masterit.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "edges")
@Getter
@Setter
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    @NotNull
    private Roadmap roadmap;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    @NotNull
    private Node source;

    @ManyToOne(optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    @NotNull
    private Node target;

    @Column(nullable = false)
    private boolean animated;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "marker_end_type")
    private String markerEndType;

    @Column(name = "marker_end_width")
    private Integer markerEndWidth;

    @Column(name = "marker_end_height")
    private Integer markerEndHeight;
}
