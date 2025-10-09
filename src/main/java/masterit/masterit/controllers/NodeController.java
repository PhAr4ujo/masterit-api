package masterit.masterit.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import masterit.masterit.dtos.input.NodeInputDTO;
import masterit.masterit.entities.Node;
import masterit.masterit.entities.Roadmap;
import masterit.masterit.entities.User;
import masterit.masterit.services.interfaces.INodeService;
import masterit.masterit.services.interfaces.IRoadmapService;
import masterit.masterit.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nodes")
public class NodeController {

    private final INodeService nodeService;
    private final IUserService userService;
    private final IRoadmapService roadmapService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean ownsRoadmap(User user, Roadmap roadmap) {
        return roadmap.getUser().getId().equals(user.getId());
    }

    /**
     * List all nodes (no ownership restriction here)
     */
    @GetMapping
    public ResponseEntity<?> index(@RequestParam UUID roadmapId) {
        User currentUser = getCurrentUser();

        Roadmap roadmap = roadmapService.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view nodes of someone else's roadmap.");
        }

        List<Node> nodes = nodeService.index(roadmapId);
        return ResponseEntity.ok(nodes);
    }

    /**
     * Create a new node (must own the roadmap)
     */
    @PostMapping
    public ResponseEntity<?> store(@Valid @RequestBody NodeInputDTO dto) {
        User currentUser = getCurrentUser();

        Roadmap roadmap = roadmapService.findById(dto.getRoadmapId())
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot create a node in someone else's roadmap.");
        }

        Node node = new Node();
        node.setPositionX(dto.getPositionX());
        node.setPositionY(dto.getPositionY());
        node.setLabel(dto.getLabel());
        node.setDescription(dto.getDescription());
        node.setStatus(dto.getStatus());
        node.setPriority(dto.getPriority());
        node.setRoadmap(roadmap);

        Node created = nodeService.store(node);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update a node (must own the roadmap of that node)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody NodeInputDTO dto) {
        User currentUser = getCurrentUser();

        Node existing = nodeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Node not found"));

        if (!ownsRoadmap(currentUser, existing.getRoadmap())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot update a node in someone else's roadmap.");
        }

        Roadmap roadmap = roadmapService.findById(dto.getRoadmapId())
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot move a node to someone else's roadmap.");
        }

        existing.setPositionX(dto.getPositionX());
        existing.setPositionY(dto.getPositionY());
        existing.setLabel(dto.getLabel());
        existing.setDescription(dto.getDescription());
        existing.setStatus(dto.getStatus());
        existing.setPriority(dto.getPriority());
        existing.setRoadmap(roadmap);

        Node updated = nodeService.update(id, existing);
        return ResponseEntity.ok(updated);
    }

    /**
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Node existing = nodeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Node not found"));

        if (!ownsRoadmap(currentUser, existing.getRoadmap())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot delete a node in someone else's roadmap.");
        }

        nodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
