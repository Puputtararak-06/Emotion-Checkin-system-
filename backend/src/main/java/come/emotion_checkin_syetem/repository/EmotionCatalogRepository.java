package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.EmotionCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 📍 LOCATION: src/main/java/come/emotion_checkin_syetem/repository/EmotionCatalogRepository.java
 *
 * 🎭 EMOTION CATALOG REPOSITORY - Master data สำหรับอารมณ์
 */
@Repository
public interface EmotionCatalogRepository extends JpaRepository<EmotionCatalog, Long> {

    List<EmotionCatalog> findByLevel(Integer level);

    Optional<EmotionCatalog> findByNameAndLevel(String name, Integer level);

    List<EmotionCatalog> findAllByOrderByLevelAsc();

    @Query("SELECT DISTINCT e.colorCode FROM EmotionCatalog e WHERE e.level = :level")
    List<String> findColorCodesByLevel(@Param("level") Integer level);
}