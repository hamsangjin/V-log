package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional
    public boolean toggleLike(Long postId, User user) {
        Like like = likeRepository.findByPostIdAndUserId(postId, user.getId());
        if (like != null) {
            likeRepository.delete(like);
            return false;
        } else {
            like = new Like();
            like.setUser(user);
            like.setPost(new Post(postId));
            likeRepository.save(like);
            return true;
        }
    }

    public boolean isLikedByUser(Long postId, User user) {
        return likeRepository.findByPostIdAndUserId(postId, user.getId()) != null;
    }
}
