package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import masterit.masterit.entities.Diary;
import masterit.masterit.repositories.DiaryRepository;
import masterit.masterit.services.interfaces.IDiaryService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class DiaryService implements IDiaryService {

    private final DiaryRepository diaryRepository;

    @Override
    public Optional<Diary> findById(UUID id) {
        return diaryRepository.findById(id); // Fixed: removed Optional.ofNullable wrapper
    }

    @Override
    public List<Diary> findByRoadmapId(UUID roadmapId) {
        return diaryRepository.findByRoadmapId(roadmapId);
    }

    @Override
    public Diary store(Diary diary) {
        return diaryRepository.save(diary); // Fixed: added missing parenthesis
    }

    @Override
    public Diary update(UUID id, Diary diary) {
        return diaryRepository.findById(id)
                .map(existing -> {
                    // Update fields here if needed
                    existing.setRoadmap(diary.getRoadmap());
                    return diaryRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Diary not found with id " + id));
    }

    @Override
    public void delete(UUID id) {
        diaryRepository.deleteById(id);
    }
}