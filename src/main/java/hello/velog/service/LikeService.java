package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, User user) {
        return likeRepository.findByPostIdAndUserId(postId, user.getId()) != null;
    }

    @Transactional(readOnly = true)
    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }

    // 탈퇴할 유저의 좋아요 삭제
    @Transactional
    public void deleteLikesByUser(Long userId) {
        likeRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteByPostId(Long postId) {
        likeRepository.deleteByPostId(postId);
    }
}
