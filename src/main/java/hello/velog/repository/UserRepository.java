package hello.velog.repository;

import hello.velog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    long countByFollowers_Id(Long userId); // 팔로워 수
    long countByFollowing_Id(Long userId); // 팔로잉 수
}
