package masterit.masterit.services.interfaces;

import masterit.masterit.entities.Diary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDiaryService {
    Optional<Diary> findById(UUID id);
    List<Diary> findByRoadmapId(UUID roadmapId); // Add this - to get all diaries for a roadmap
    Diary store(Diary diary);
    Diary update(UUID id, Diary diary);
    void delete(UUID id);
}