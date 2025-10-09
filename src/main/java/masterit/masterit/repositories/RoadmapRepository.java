package masterit.masterit.repositories;

import masterit.masterit.entities.Roadmap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoadmapRepository extends JpaRepository<Roadmap, UUID> {
    Page<Roadmap> findAllByUserId(UUID userId, Pageable pageable);
}
