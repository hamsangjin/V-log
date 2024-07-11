package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    @Transactional(readOnly = true)
    public Blog findBlogByUserId(Long userId) {
        return blogRepository.findById(userId).orElse(null);
    }
}
