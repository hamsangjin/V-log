package hello.velog.repository;

import hello.velog.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    boolean existsByTitle(String title);
}