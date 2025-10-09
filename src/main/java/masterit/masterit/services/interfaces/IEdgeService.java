package masterit.masterit.services.interfaces;

import masterit.masterit.entities.Edge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEdgeService {
    List<Edge> index(UUID roadmapId);
    Optional<Edge> findById(UUID id);
    Edge store(Edge edge);
    Edge update(UUID id, Edge edge);
    void delete(UUID id);
}
