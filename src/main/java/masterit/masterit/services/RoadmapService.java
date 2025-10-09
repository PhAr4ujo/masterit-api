package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import masterit.masterit.entities.Roadmap;
import masterit.masterit.repositories.RoadmapRepository;
import masterit.masterit.services.interfaces.IRoadmapService;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class RoadmapService implements IRoadmapService {

    private final RoadmapRepository roadmapRepository;

    @Override
    public Page<Roadmap> getByUserId(UUID userId, Pageable pageable) {
        return roadmapRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public List<Roadmap> index() {
        return roadmapRepository.findAll();
    }

    @Override
    public Optional<Roadmap> findById(UUID id) {
        return roadmapRepository.findById(id);
    }

    @Override
    public Roadmap store(Roadmap roadmap) {
        return roadmapRepository.save(roadmap);
    }

    @Override
    public Roadmap update(UUID id, Roadmap roadmap) {
        return roadmapRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(roadmap.getTitle());
                    existing.setDescription(roadmap.getDescription());
                    existing.setUser(roadmap.getUser());
                    return roadmapRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Roadmap not found with id " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!roadmapRepository.existsById(id)) {
            throw new RuntimeException("Roadmap not found with id " + id);
        }
        roadmapRepository.deleteById(id);
    }
}
