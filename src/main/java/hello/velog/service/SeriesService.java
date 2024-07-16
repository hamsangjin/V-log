package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.exception.SeriesAlreadyExistsException;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;
    private final PostRepository postRepository;

    public List<Series> findAllSeries() {
        return seriesRepository.findAll();
    }

    public Series findById(Long id) {
        return seriesRepository.findById(id).orElse(null);
    }

    @Transactional
    public Series saveSeries(Series series) {
        if (seriesRepository.existsByTitleAndBlogId(series.getTitle(), series.getBlog().getId())) {
            throw new SeriesAlreadyExistsException("시리즈가 중복되었습니다.");
        }
        return seriesRepository.save(series);
    }

    @Transactional
    public Series updateSeries(Series series) {
        return seriesRepository.save(series);
    }

    @Transactional
    public Series handleSeriesCreationOrUpdate(String newSeries, Long seriesId, User user) {
        if (newSeries != null && !newSeries.trim().isEmpty()) {
            Series series = new Series();
            series.setTitle(newSeries);
            series.setBlog(user.getBlog());
            saveSeries(series);
            return series;
        } else if (seriesId != null) {
            Series series = findById(seriesId);
            series.setLastUpdated(LocalDateTime.now());
            updateSeries(series);
            return series;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Series> findAllSeriesByBlogId(Long blogId) {
        return seriesRepository.findByBlogId(blogId);
    }

    @Transactional(readOnly = true)
    public Post findFirstPostBySeriesId(Long seriesId) {
        List<Post> posts = seriesRepository.findFirstPostBySeriesId(seriesId);
        return posts.isEmpty() ? null : posts.get(0);
    }

    public List<Map<String, Object>> findAllSeriesWithThumbnailByBlogId(Long blogId) {
        List<Series> seriesList = seriesRepository.findByBlogId(blogId);

        return seriesList.stream().map(series -> {
            Map<String, Object> seriesMap = new HashMap<>();
            seriesMap.put("series", series);

            // 시리즈의 첫 번째 게시물의 썸네일 이미지를 가져옴
            Post firstPost = postRepository.findFirstBySeriesOrderByCreatedAtAsc(series);
            seriesMap.put("thumbnailImage", firstPost != null ? firstPost.getThumbnailImage() : "/images/post/default-image.png");

            return seriesMap;
        }).collect(Collectors.toList());
    }
}
