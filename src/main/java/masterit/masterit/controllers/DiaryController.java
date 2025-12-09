package masterit.masterit.controllers;

import lombok.RequiredArgsConstructor;
import masterit.masterit.entities.Diary;
import masterit.masterit.entities.Roadmap;
import masterit.masterit.entities.User;
import masterit.masterit.services.interfaces.IDiaryService;
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
@RequestMapping("/diaries")
public class DiaryController {

    private final IDiaryService diaryService;
    private final IRoadmapService roadmapService;
    private final IUserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("User not logged in");
        }

        String email = auth.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    private boolean ownsRoadmap(User user, Roadmap roadmap) {
        return roadmap.getUser().getId().equals(user.getId());
    }

    /** Get a single diary by ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Diary diary = diaryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        if (!ownsRoadmap(currentUser, diary.getRoadmap())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view someone else's diary.");
        }

        return ResponseEntity.ok(diary);
    }

    /** List diaries for a roadmap (ownership enforced) */
    @GetMapping
    public ResponseEntity<?> index(@RequestParam UUID roadmapId) {
        User currentUser = getCurrentUser();

        Roadmap roadmap = roadmapService.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view diaries of someone else's roadmap.");
        }

        List<Diary> diaries = diaryService.findByRoadmapId(roadmapId);
        return ResponseEntity.ok(diaries);
    }

    /** Create a new diary (must own the roadmap) */
    @PostMapping
    public ResponseEntity<?> store(@RequestParam UUID roadmapId) {
        System.out.println("Received roadmapId: " + roadmapId);
        User currentUser = getCurrentUser();
        System.out.println("Current user: " + currentUser.getEmail());


        Roadmap roadmap = roadmapService.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        if (!ownsRoadmap(currentUser, roadmap)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot create a diary in someone else's roadmap.");
        }

        Diary diary = new Diary();
        diary.setRoadmap(roadmap);

        Diary created = diaryService.store(diary);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Delete a diary (must own the roadmap) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        Diary existing = diaryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        if (!ownsRoadmap(currentUser, existing.getRoadmap())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot delete someone else's diary.");
        }

        diaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}