package hello.velog.service;

import hello.velog.controller.LikeController;
import hello.velog.domain.*;
import hello.velog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;


    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    public List<Post> findPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Post> getUserPosts(Long userId, Boolean privacySetting, Boolean temporarySetting) {
        List<Post> posts;
        if (privacySetting != null) {
            posts = postRepository.findByUserIdAndPrivacySettingAndTemporarySetting(
                    userId, privacySetting, temporarySetting);
        } else{
            posts = postRepository.findByUserIdAndTemporarySetting(userId, temporarySetting);
        }

        return posts.stream().peek(post -> {
            post.setTags(new HashSet<>(tagRepository.findByPostsId(post.getId())));
            post.setLikes(new HashSet<>(likeRepository.findByPostId(post.getId())));
        }).collect(Collectors.toList());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // 포스트 상세보기를 위한 메소드
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
    }
}
