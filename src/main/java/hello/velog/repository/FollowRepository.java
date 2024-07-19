package hello.velog.repository;

import hello.velog.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee.id = :userId")
    int countFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    int countFollowingByUserId(@Param("userId") Long userId);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFolloweeId(Long followerId);

    void deleteByFollowerId(Long followerId);
}
