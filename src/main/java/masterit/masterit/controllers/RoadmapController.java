package masterit.masterit.controllers;

import lombok.RequiredArgsConstructor;
import masterit.masterit.dtos.input.RoadmapInputDTO;
import masterit.masterit.entities.Roadmap;
import masterit.masterit.entities.User;
import masterit.masterit.services.interfaces.IRoadmapService;
import masterit.masterit.services.interfaces.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roadmaps")
public class RoadmapController {

    private final IRoadmapService roadmapService;
    private final IUserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<?> index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = getCurrentUser();
        UUID userId = currentUser.getId();

        Page<Roadmap> roadmaps = roadmapService.getByUserId(userId, PageRequest.of(page, size));

        return ResponseEntity.ok(Map.of(
                "data", roadmaps.getContent(),
                "pagination", Map.of(
                        "page", roadmaps.getNumber(),
                        "size", roadmaps.getSize(),
                        "totalElements", roadmaps.getTotalElements(),
                        "totalPages", roadmaps.getTotalPages()
                )
        ));
    }

    @PostMapping
    public ResponseEntity<?> store(@Valid @RequestBody RoadmapInputDTO dto) {
        User currentUser = getCurrentUser();

        Roadmap roadmap = new Roadmap();
        roadmap.setTitle(dto.getTitle());
        roadmap.setDescription(dto.getDescription());
        roadmap.setUser(currentUser);

        Roadmap created = roadmapService.store(roadmap);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody RoadmapInputDTO dto) {
        User currentUser = getCurrentUser();

        Roadmap existing = roadmapService.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!existing.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot update someone else's roadmap.");
        }

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());

        Roadmap updated = roadmapService.update(id, existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Roadmap existing = roadmapService.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!existing.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot delete someone else's roadmap.");
        }

        roadmapService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Optional<Roadmap> roadmapOpt = roadmapService.findById(id);
        if (roadmapOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Roadmap not found");
        }

        Roadmap roadmap = roadmapOpt.get();

        if (!roadmap.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view someone else's roadmap.");
        }

        return ResponseEntity.ok(roadmap);
    }

}
