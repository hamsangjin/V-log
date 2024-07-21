package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.exception.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final SeriesService seriesService;

    @Transactional(readOnly = true)
    public Blog findBlogByUserId(Long userId) {
        return blogRepository.findById(userId)
                .orElseThrow(() -> new BlogNotFoundException("블로그를 찾을 수 없습니다."));
    }

    @Transactional
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Transactional
    public void updateInfo(Blog blog, String newInfo) {
        if (newInfo != null && !newInfo.trim().isEmpty())       blog.setInfo(newInfo);

        blogRepository.save(blog);
    }

    @Transactional
    public void updateTitle(Blog blog, String newTitle) {
        if (newTitle != null && !newTitle.trim().isEmpty())     blog.setTitle(newTitle);

        blogRepository.save(blog);
    }

    // 탈퇴할 유저의 블로그 삭제
    @Transactional
    public void deleteBlogsAndSeriesByUser(Long userId) {
        Blog findBlog = blogRepository.findByUserId(userId);    // userId와 연결된 블로그 정보 조회
        seriesService.deleteSeries(findBlog.getId());           // 해당 블로그와 연결된 시리즈 삭제
        blogRepository.deleteByUserId(userId);                  // 유저와 연결된 블로그 삭제
    }
}
