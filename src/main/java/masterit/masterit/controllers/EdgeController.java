package masterit.masterit.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import masterit.masterit.dtos.input.EdgeInputDTO;
import masterit.masterit.entities.Edge;
import masterit.masterit.entities.Node;
import masterit.masterit.entities.Roadmap;
import masterit.masterit.entities.User;
import masterit.masterit.services.interfaces.IEdgeService;
import masterit.masterit.services.interfaces.INodeService;
import masterit.masterit.services.interfaces.IRoadmapService;
import masterit.masterit.services.interfaces.IUserService;
import org.aspectj.weaver.Dump;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/edges")
public class EdgeController {

    private final IEdgeService edgeService;
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

    /** List edges for a roadmap (ownership enforced) */
    @GetMapping
    public ResponseEntity<?> index(@RequestParam UUID roadmapId) {
        User currentUser = getCurrentUser();

        Roadmap roadmap = roadmapService.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view edges of someone else's roadmap.");
        }

        List<Edge> edges = edgeService.index(roadmapId);
        return ResponseEntity.ok(edges);
    }

    /** Create a new edge (must own the roadmap) */
    @PostMapping
    public ResponseEntity<?> store(@Valid @RequestBody EdgeInputDTO dto) {
        User currentUser = getCurrentUser();

        Roadmap roadmap = roadmapService.findById(dto.getRoadmapId())
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot create an edge in someone else's roadmap.");
        }

        Edge edge = new Edge();
        edge.setRoadmap(roadmap);
        Node sourceNode = nodeService.findById(dto.getSourceId())
                .orElseThrow(() -> new RuntimeException("Source node not found"));

        Node targetNode = nodeService.findById(dto.getTargetId())
                .orElseThrow(() -> new RuntimeException("Target node not found"));

        edge.setSource(sourceNode);
        edge.setTarget(targetNode);

        edge.setAnimated(dto.getAnimated());
        edge.setType(dto.getType());
        edge.setMarkerEndType(dto.getMarkerEndType());
        edge.setMarkerEndWidth(dto.getMarkerEndWidth());
        edge.setMarkerEndHeight(dto.getMarkerEndHeight());

        Edge created = edgeService.store(edge);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Update an edge (must own the roadmap) */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody EdgeInputDTO dto) {
        User currentUser = getCurrentUser();

        Edge existing = edgeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Edge not found"));

        if (!ownsRoadmap(currentUser, existing.getRoadmap())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot update an edge in someone else's roadmap.");
        }

        Roadmap roadmap = roadmapService.findById(dto.getRoadmapId())
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot move an edge to someone else's roadmap.");
        }

        // Fetch source and target nodes
        Node sourceNode = nodeService.findById(dto.getSourceId())
                .orElseThrow(() -> new RuntimeException("Source node not found"));
        Node targetNode = nodeService.findById(dto.getTargetId())
                .orElseThrow(() -> new RuntimeException("Target node not found"));

        existing.setRoadmap(roadmap);
        existing.setSource(sourceNode);   // <-- assign Node entity
        existing.setTarget(targetNode);   // <-- assign Node entity
        existing.setAnimated(dto.getAnimated());
        existing.setType(dto.getType());
        existing.setMarkerEndType(dto.getMarkerEndType());
        existing.setMarkerEndWidth(dto.getMarkerEndWidth());
        existing.setMarkerEndHeight(dto.getMarkerEndHeight());

        Edge updated = edgeService.update(id, existing);
        return ResponseEntity.ok(updated);
    }


    /** Delete an edge (must own the roadmap) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Edge existing = edgeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Edge not found"));

        if (!ownsRoadmap(currentUser, existing.getRoadmap())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot delete an edge in someone else's roadmap.");
        }

        edgeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
