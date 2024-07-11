package hello.velog.service;

import hello.velog.domain.Series;
import hello.velog.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {
    private final SeriesRepository seriesRepository;

    public List<Series> findAllSeries() {
        return seriesRepository.findAll();
    }

    public Series findById(Long id) {
        return seriesRepository.findById(id).orElse(null);
    }

    public Series saveSeries(Series series) {
        if (seriesRepository.existsByTitle(series.getTitle())) {
            throw new IllegalArgumentException("시리즈가 중복되었습니다.");
        }
        return seriesRepository.save(series);
    }
}