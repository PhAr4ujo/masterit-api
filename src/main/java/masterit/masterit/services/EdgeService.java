package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import masterit.masterit.entities.Edge;
import masterit.masterit.repositories.EdgeRepository;
import masterit.masterit.services.interfaces.IEdgeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class EdgeService implements IEdgeService {

    private final EdgeRepository edgeRepository;

    @Override
    public List<Edge> index(UUID roadmapId) {
        return edgeRepository.findAllByRoadmapId(roadmapId);
    }

    @Override
    public Optional<Edge> findById(UUID id) {
        return Optional.ofNullable(edgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Edge not found")));
    }

    @Override
    public Edge store(Edge edge) {
        return edgeRepository.save(edge);
    }

    @Override
    public Edge update(UUID id, Edge edge) {
        return edgeRepository.findById(id)
                .map(existing -> {
                    existing.setAnimated(edge.isAnimated());
                    existing.setType(edge.getType());
                    existing.setMarkerEndType(edge.getMarkerEndType());
                    existing.setMarkerEndWidth(edge.getMarkerEndWidth());
                    existing.setMarkerEndHeight(edge.getMarkerEndHeight());
                    existing.setSource(edge.getSource());
                    existing.setTarget(edge.getTarget());
                    existing.setRoadmap(edge.getRoadmap());
                    return edgeRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Edge not found with id " + id));
    }


    @Override
    public void delete(UUID id) {
        edgeRepository.deleteById(id);
    }
}
