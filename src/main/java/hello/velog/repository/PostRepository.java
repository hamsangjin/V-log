package hello.velog.repository;

import hello.velog.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByUserIdAndPrivacySettingAndTemporarySetting(Long userId, boolean privacySetting, boolean temporarySetting);
    List<Post> findByUserIdAndTemporarySetting(Long userId, boolean temporarySetting);
    Post findFirstBySeriesOrderByCreatedAtAsc(Series series);
    List<Post> findByUserIdIn(List<Long> userIds);
    @Query("SELECT p FROM Post p WHERE p.privacySetting = false AND p.temporarySetting = false ORDER BY size(p.likes) DESC")
    List<Post> findAllByOrderByLikesDesc();

    @Query("SELECT p FROM Post p WHERE p.privacySetting = false AND p.temporarySetting = false ORDER BY p.createdAt DESC")
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p WHERE p.userId IN :userIds AND p.privacySetting = false AND p.temporarySetting = false ORDER BY p.createdAt DESC")
    List<Post> findByUserIdInAndOrderByCreatedAtDesc(List<Long> userIds);
}