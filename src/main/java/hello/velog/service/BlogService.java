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

    @Transactional(readOnly = true)
    public Blog findBlogByUserId(Long userId) {
        // 먼저 해당 사용자가 존재하는지 확인합니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자가 존재하면 사용자의 블로그를 찾습니다.
        return blogRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("블로그를 찾을 수 없습니다."));
    }
}
