package hello.velog.repository;

import hello.velog.domain.*;
import hello.velog.dto.TagCount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.userId = :userId AND t.name = :tagName AND (:privacySetting IS NULL OR p.privacySetting = :privacySetting) AND (:temporarySetting IS NULL OR p.temporarySetting = :temporarySetting)")
    List<Post> findByUserIdAndTagName(@Param("userId") Long userId, @Param("tagName") String tagName, @Param("privacySetting") Boolean privacySetting, @Param("temporarySetting") Boolean temporarySetting);

    @Query("SELECT DISTINCT t.name FROM Post p JOIN p.tags t WHERE p.userId = :userId")
    List<String> findTagsByUserId(@Param("userId") Long userId);

    @Query("SELECT new hello.velog.dto.TagCount(t.id, t.name, COUNT(pt)) " +
            "FROM PostTag pt " +
            "JOIN pt.tag t " +
            "JOIN pt.post p " +
            "WHERE p.userId = :userId " +
            "AND p.temporarySetting = false " +
            "AND (:isBlogOwner = true OR p.privacySetting = false) " +
            "GROUP BY t.id, t.name")
    List<TagCount> findTagsWithCountByUserId(@Param("userId") Long userId, @Param("isBlogOwner") boolean isBlogOwner);
}