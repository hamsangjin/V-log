package hello.velog.repository;

import hello.velog.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM Series s WHERE s.title = :title AND s.blog.id = :blogId")
    boolean existsByTitleAndBlogId(String title, Long blogId);
    List<Series> findByBlogId(Long blogId);
    @Query("SELECT p FROM Post p WHERE p.series.id = :seriesId ORDER BY p.createdAt ASC")
    List<Post> findFirstPostBySeriesId(Long seriesId);
}