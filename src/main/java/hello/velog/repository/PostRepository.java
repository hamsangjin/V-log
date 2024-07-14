package hello.velog.repository;

import hello.velog.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByUserIdAndPrivacySettingAndTemporarySetting(Long userId, boolean privacySetting, boolean temporarySetting);
    List<Post> findByUserIdAndTemporarySetting(Long userId, boolean temporarySetting);
    Post findFirstBySeriesOrderByCreatedAtAsc(Series series);
}