package come.emotion_checkin_syetem.repository;

import come.emotion_checkin_syetem.entity.EmotionCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Emotion Catalog Repository
 */
@Repository
public interface EmotionCatalogRepository extends JpaRepository<EmotionCatalog, Long> {
    
    /**
     * หารายการอารมณ์ตาม level
     */
    List<EmotionCatalog> findByLevel(Integer level);
    
    /**
     * หาอารมณ์จากชื่อและ level
     */
    Optional<EmotionCatalog> findByNameAndLevel(String name, Integer level);
    
    /**
     * หารายการอารมณ์ทั้งหมดเรียงตาม level
     */
    List<EmotionCatalog> findAllByOrderByLevelAsc();
}