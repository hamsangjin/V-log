package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    public List<Post> findPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    public List<Post> getUserPosts(Long userId, Boolean privacySetting, Boolean temporarySetting) {
        if (privacySetting == null)     return postRepository.findByUserIdAndTemporarySetting(userId, temporarySetting);
        return postRepository.findByUserIdAndPrivacySettingAndTemporarySetting(userId, privacySetting, temporarySetting);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // 포스트 상세보기를 위한 메소드
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
    }
}
