package masterit.masterit.repositories;

import masterit.masterit.entities.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiaryRepository extends JpaRepository<Diary, UUID> {
    List<Diary> findByRoadmapId(UUID roadmapId);
}