package masterit.masterit.services.interfaces;

import masterit.masterit.entities.Node;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface INodeService {
    Node store(Node node);
    Node update(UUID id, Node node);
    void delete(UUID id);
    Optional<Node> findById(UUID id);
    List<Node> index(UUID roadmapId);
}
