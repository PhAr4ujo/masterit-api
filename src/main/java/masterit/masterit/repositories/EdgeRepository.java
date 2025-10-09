package masterit.masterit.repositories;

import masterit.masterit.entities.Edge;
import masterit.masterit.entities.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EdgeRepository extends JpaRepository<Edge, UUID> {
    List<Edge> findAllByRoadmapId(UUID roadmapId);
    @Modifying
    @Transactional
    void deleteBySourceOrTarget(Node source, Node target);
}
