package masterit.masterit.services.interfaces;

import masterit.masterit.entities.DiaryPage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDiaryPageService {
    Optional<DiaryPage> findById(UUID id);
    List<DiaryPage> findByDiaryId(UUID diaryId); // Add this - to get all pages for a diary
    DiaryPage store(DiaryPage diaryPage);
    DiaryPage update(UUID id, DiaryPage diaryPage);
    void delete(UUID id);
    public DiaryPage updateSimple(UUID id, DiaryPage diaryPage);
}