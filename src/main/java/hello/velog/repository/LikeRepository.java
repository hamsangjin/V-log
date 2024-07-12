package hello.velog.repository;

import hello.velog.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByPostIdAndUserId(Long postId, Long userId);
    List<Like> findByPostId(Long postId);
}
