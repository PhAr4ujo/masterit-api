package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import masterit.masterit.entities.Node;
import masterit.masterit.repositories.EdgeRepository;
import masterit.masterit.repositories.NodeRepository;
import masterit.masterit.services.interfaces.INodeService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class NodeService implements INodeService {

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    @Override
    public List<Node> index(UUID roadmapId) {
        return nodeRepository.findAllByRoadmapId(roadmapId);
    }

    @Override
    public Node store(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    public Node update(UUID id, Node node) {
        return nodeRepository.findById(id)
                .map(existing -> {
                    existing.setPositionX(node.getPositionX());
                    existing.setPositionY(node.getPositionY());
                    existing.setLabel(node.getLabel());
                    existing.setDescription(node.getDescription());
                    existing.setStatus(node.getStatus());
                    existing.setPriority(node.getPriority());
                    existing.setRoadmap(node.getRoadmap());
                    return nodeRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Node not found with id " + id));
    }

    @Override
    public void delete(UUID id) {
        Node node = nodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Node not found"));

        // Delete edges connected to this node
        edgeRepository.deleteBySourceOrTarget(node, node);

        // Delete the node itself
        nodeRepository.delete(node);
    }

    @Override
    public Optional<Node> findById(UUID id) {
        return nodeRepository.findById(id);
    }
}
