package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final SeriesService seriesService;

    @Transactional(readOnly = true)
    public Blog findBlogByUserId(Long userId) {
        // 먼저 해당 사용자가 존재하는지 확인합니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자가 존재하면 사용자의 블로그를 찾습니다.
        return blogRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("블로그를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Blog findBlogByUsername(String username) {
        // 먼저 해당 사용자가 존재하는지 확인합니다.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자가 존재하면 사용자의 블로그를 찾습니다.
        return blogRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("블로그를 찾을 수 없습니다."));
    }

    @Transactional
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    // 탈퇴할 유저의 블로그 삭제
    @Transactional
    public void deleteBlogsAndSeriesByUser(Long userId) {

        // userId와 연결된 블로그 정보 조회
        Blog findBlog = blogRepository.findByUserId(userId);
        // 해당 블로그와 연결된 시리즈 삭제
        seriesService.deleteSeries(findBlog.getId());

        // 유저와 연결된 블로그 삭제
        blogRepository.deleteByUserId(userId);
    }
}
