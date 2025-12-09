package masterit.masterit.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import masterit.masterit.dtos.input.UpdatePageRequest;
import masterit.masterit.entities.Diary;
import masterit.masterit.entities.DiaryPage;
import masterit.masterit.entities.User;
import masterit.masterit.services.interfaces.IDiaryPageService;
import masterit.masterit.services.interfaces.IDiaryService;
import masterit.masterit.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary-pages")
public class DiaryPageController {

    private final IDiaryPageService diaryPageService;
    private final IDiaryService diaryService;
    private final IUserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean ownsDiary(User user, Diary diary) {
        return diary.getRoadmap().getUser().getId().equals(user.getId());
    }

    /** Get a single page by ID */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        DiaryPage page = diaryPageService.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));

        if (!ownsDiary(currentUser, page.getDiary())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view someone else's diary page.");
        }

        return ResponseEntity.ok(page);
    }

    /** List pages for a diary (ownership enforced) */
    @GetMapping
    public ResponseEntity<?> index(@RequestParam UUID diaryId) {
        User currentUser = getCurrentUser();

        Diary diary = diaryService.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        if (!ownsDiary(currentUser, diary)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot view pages of someone else's diary.");
        }

        List<DiaryPage> pages = diaryPageService.findByDiaryId(diaryId);
        return ResponseEntity.ok(pages);
    }

    /** Create a new page (must own the diary) */
    @PostMapping
    public ResponseEntity<?> store(@RequestParam UUID diaryId,
                                   @RequestParam(required = false) UUID rearPageId,
                                   @RequestBody(required = false) Map<String, String> requestBody) {
        User currentUser = getCurrentUser();

        Diary diary = diaryService.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        if (!ownsDiary(currentUser, diary)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot create a page in someone else's diary.");
        }

        DiaryPage page = new DiaryPage();
        page.setDiary(diary);

        // Set title from request body or use default
        String title = "Untitled";
        if (requestBody != null && requestBody.containsKey("title")) {
            title = requestBody.get("title");
            if (title == null || title.trim().isEmpty()) {
                title = "Untitled";
            }
        }
        page.setTitle(title);

        // Link to previous page if provided
        if (rearPageId != null) {
            DiaryPage rearPage = diaryPageService.findById(rearPageId)
                    .orElseThrow(() -> new RuntimeException("Rear page not found"));

            if (!ownsDiary(currentUser, rearPage.getDiary())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Cannot link to someone else's page.");
            }

            page.setRearPage(rearPage);
        }

        DiaryPage created = diaryPageService.store(page);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Update a page (must own the diary) - Using DTO */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @RequestBody UpdatePageRequest request) {
        User currentUser = getCurrentUser();

        DiaryPage existing = diaryPageService.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));

        if (!ownsDiary(currentUser, existing.getDiary())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot update someone else's page.");
        }

        // Update title if provided
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }

        // Update content if provided
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        } else {
            existing.setContent("[]");
        }

        // Save using service (make sure your service doesn't modify content)
        DiaryPage updated = diaryPageService.updateSimple(id, existing);
        return ResponseEntity.ok(updated);
    }

    /** Delete a page (must own the diary) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User currentUser = getCurrentUser();

        DiaryPage existing = diaryPageService.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));

        if (!ownsDiary(currentUser, existing.getDiary())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot delete someone else's diary page.");
        }

        diaryPageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}