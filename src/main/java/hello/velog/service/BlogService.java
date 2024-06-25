package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    public Blog findBlogByUserId(Long userId) {
        return blogRepository.findById(userId).orElse(null);
    }
}
