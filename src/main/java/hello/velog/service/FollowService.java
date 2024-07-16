package hello.velog.service;

import hello.velog.domain.*;
import hello.velog.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;

    @Transactional
    public boolean toggleFollow(User follower, User followee) {
        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee).orElse(null);
        if (follow != null) {
            followRepository.delete(follow);
            return false;
        } else {
            follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowee(followee);
            followRepository.save(follow);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(User follower, User followee) {
        return followRepository.findByFollowerAndFollowee(follower, followee).isPresent();
    }

    @Transactional(readOnly = true)
    public int getFollowerCount(Long userId) {
        return followRepository.countFollowersByUserId(userId);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(Long userId) {
        return followRepository.countFollowingByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Follow> findByFollower(User follower) {
        return followRepository.findByFollower(follower);
    }
}
