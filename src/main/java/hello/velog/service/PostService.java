package hello.velog.service;

import hello.velog.domain.Post;
import hello.velog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public List<Post> findPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }
}
