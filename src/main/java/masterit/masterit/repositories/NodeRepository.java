package masterit.masterit.repositories;

import masterit.masterit.entities.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NodeRepository extends JpaRepository<Node, UUID> {
    List<Node> findAllByRoadmapId(UUID roadmapId);
}
