package masterit.masterit.repositories;

import masterit.masterit.entities.DiaryPage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiaryPageRepository extends JpaRepository<DiaryPage, UUID> {


    // OR if you want to fetch multiple relationships
    @EntityGraph(attributePaths = {"rearPage", "diary"})
    List<DiaryPage> findByDiaryId(UUID diaryId);
}