package masterit.masterit.services.interfaces;

import masterit.masterit.entities.Roadmap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRoadmapService {
    Page<Roadmap> getByUserId(UUID userId, Pageable pageable);

    List<Roadmap> index();
    Optional<Roadmap> findById(UUID id);
    Roadmap store(Roadmap roadmap);
    Roadmap update(UUID id, Roadmap roadmap);
    void delete(UUID id);
}
