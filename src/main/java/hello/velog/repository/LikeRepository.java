package hello.velog.repository;

import hello.velog.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByPostIdAndUserId(Long postId, Long userId);
    List<Like> findByPostId(Long postId);
    void deleteByPostId(Long postId);
    @Query("SELECT l.post FROM Like l WHERE l.user.id = :userId")
    List<Post> findLikedPostsByUserId(Long userId);
}
