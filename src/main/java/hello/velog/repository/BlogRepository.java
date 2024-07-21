package hello.velog.repository;

import hello.velog.domain.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    void deleteByUserId(Long userId);

    Blog findByUserId(Long userId);
}
