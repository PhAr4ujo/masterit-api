package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import masterit.masterit.entities.DiaryPage;
import masterit.masterit.repositories.DiaryPageRepository;
import masterit.masterit.services.interfaces.IDiaryPageService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Primary
@Transactional
public class DiaryPageService implements IDiaryPageService {

    private final DiaryPageRepository diaryPageRepository;

    @Override
    public Optional<DiaryPage> findById(UUID id) {
        return diaryPageRepository.findById(id);
    }

    @Override
    public DiaryPage updateSimple(UUID id, DiaryPage diaryPage) {
        // Just save without any processing
        return diaryPageRepository.save(diaryPage);
    }

    @Override
    public List<DiaryPage> findByDiaryId(UUID diaryId) {
        List<DiaryPage> pages = diaryPageRepository.findByDiaryId(diaryId);

        if (pages.isEmpty()) {
            return pages;
        }

        return buildLinkedListOrder(pages);
    }

    private List<DiaryPage> buildLinkedListOrder(List<DiaryPage> pages) {
        // Find all pages that have a rearPage
        Map<UUID, DiaryPage> rearToPageMap = new HashMap<>();
        for (DiaryPage page : pages) {
            if (page.getRearPage() != null) {
                rearToPageMap.put(page.getRearPage().getId(), page);
            }
        }

        // Find the first page (page that is not in the rearToPageMap values)
        List<DiaryPage> firstPageCandidates = new ArrayList<>();
        for (DiaryPage page : pages) {
            if (!rearToPageMap.containsValue(page)) {
                firstPageCandidates.add(page);
            }
        }

        // If we found multiple candidates or none, fall back to creation date
        if (firstPageCandidates.size() != 1) {
            return pages.stream()
                    .sorted(Comparator.comparing(DiaryPage::getCreatedAt))
                    .toList();
        }

        DiaryPage firstPage = firstPageCandidates.get(0);

        // Traverse the linked list
        List<DiaryPage> orderedPages = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        DiaryPage current = firstPage;

        while (current != null && !visited.contains(current.getId())) {
            visited.add(current.getId());
            orderedPages.add(current);
            current = rearToPageMap.get(current.getId());

            // Safety check
            if (visited.size() > pages.size()) {
                break;
            }
        }

        return orderedPages;
    }

    @Override
    @Transactional
    public DiaryPage store(DiaryPage diaryPage) {
        // If this page has a rearPage, we need to update that rearPage's frontPage
        if (diaryPage.getRearPage() != null) {
            DiaryPage rearPage = diaryPage.getRearPage();

            // Load the full rearPage entity from database
            DiaryPage existingRearPage = diaryPageRepository.findById(rearPage.getId())
                    .orElseThrow(() -> new RuntimeException("Rear page not found with id: " + rearPage.getId()));

            // Check if rearPage belongs to the same diary
            if (!existingRearPage.getDiary().getId().equals(diaryPage.getDiary().getId())) {
                throw new RuntimeException("Cannot link pages from different diaries");
            }

            // If the rearPage already has a frontPage, we need to insert between them
            if (existingRearPage.getFrontPage() != null) {
                DiaryPage oldFrontPage = existingRearPage.getFrontPage();

                // Update oldFrontPage to point to new page as its rearPage
                oldFrontPage.setRearPage(diaryPage);
                diaryPageRepository.save(oldFrontPage);
            }

            // Set the bidirectional relationship
            diaryPage.setRearPage(existingRearPage);
            existingRearPage.setFrontPage(diaryPage);

            // Save the updated rearPage
            diaryPageRepository.save(existingRearPage);
        }

        return diaryPageRepository.save(diaryPage);
    }

    @Override
    @Transactional
    public DiaryPage update(UUID id, DiaryPage diaryPage) {
        return diaryPageRepository.findById(id)
                .map(existing -> {
                    // Update basic fields
                    existing.setTitle(diaryPage.getTitle());
                    existing.setContent(diaryPage.getContent());

                    // Only update rearPage if it's different
                    if (diaryPage.getRearPage() != null) {
                        UUID newRearPageId = diaryPage.getRearPage().getId();

                        // Check if rearPage is actually changing
                        if (existing.getRearPage() == null ||
                                !existing.getRearPage().getId().equals(newRearPageId)) {

                            // Remove existing links
                            if (existing.getRearPage() != null) {
                                DiaryPage oldRearPage = diaryPageRepository.findById(existing.getRearPage().getId())
                                        .orElseThrow(() -> new RuntimeException("Old rear page not found"));
                                oldRearPage.setFrontPage(null);
                                diaryPageRepository.save(oldRearPage);
                            }

                            // Set up new links
                            DiaryPage newRearPage = diaryPageRepository.findById(newRearPageId)
                                    .orElseThrow(() -> new RuntimeException("New rear page not found"));

                            // Check diary consistency
                            if (!newRearPage.getDiary().getId().equals(existing.getDiary().getId())) {
                                throw new RuntimeException("Cannot link pages from different diaries");
                            }

                            // If new rearPage already has a frontPage, insert between
                            if (newRearPage.getFrontPage() != null) {
                                DiaryPage oldFrontOfNewRear = newRearPage.getFrontPage();
                                oldFrontOfNewRear.setRearPage(existing);
                                diaryPageRepository.save(oldFrontOfNewRear);
                            }

                            // Set bidirectional relationship
                            existing.setRearPage(newRearPage);
                            newRearPage.setFrontPage(existing);
                            diaryPageRepository.save(newRearPage);
                        }
                    } else if (existing.getRearPage() != null) {
                        // Removing the rearPage link
                        DiaryPage oldRearPage = diaryPageRepository.findById(existing.getRearPage().getId())
                                .orElseThrow(() -> new RuntimeException("Old rear page not found"));
                        oldRearPage.setFrontPage(null);
                        diaryPageRepository.save(oldRearPage);
                        existing.setRearPage(null);
                    }

                    return diaryPageRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("DiaryPage not found with id " + id));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        diaryPageRepository.findById(id).ifPresent(page -> {
            // Handle linked list updates

            // If this page has a rearPage, update its frontPage
            if (page.getRearPage() != null) {
                DiaryPage rearPage = diaryPageRepository.findById(page.getRearPage().getId())
                        .orElseThrow(() -> new RuntimeException("Rear page not found"));

                rearPage.setFrontPage(page.getFrontPage());
                diaryPageRepository.save(rearPage);
            }

            // If this page has a frontPage, update its rearPage
            if (page.getFrontPage() != null) {
                DiaryPage frontPage = diaryPageRepository.findById(page.getFrontPage().getId())
                        .orElseThrow(() -> new RuntimeException("Front page not found"));

                frontPage.setRearPage(page.getRearPage());
                diaryPageRepository.save(frontPage);
            }

            // Now delete the page
            diaryPageRepository.deleteById(id);
        });
    }
}