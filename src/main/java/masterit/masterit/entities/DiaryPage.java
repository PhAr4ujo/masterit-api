package masterit.masterit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diary_pages")
@Getter
@Setter
public class DiaryPage {

    public DiaryPage() {
        this.title = "Untitled";
        this.content = "[]"; // Empty JSON array for TipTap
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "diary_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Diary diary;

    @Column(name = "title", length = 255)
    private String title;

    @ManyToOne
    @JoinColumn(name = "rear_page_id")
    private DiaryPage rearPage;

    @OneToOne(mappedBy = "rearPage")
    @JsonIgnore
    private DiaryPage frontPage;

    @Column(name = "content", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String content;

    // Timestamps - these will be automatically managed by Hibernate
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}