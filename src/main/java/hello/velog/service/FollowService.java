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

    // 유저의 팔로워 수 조회
    @Transactional(readOnly = true)
    public int getFollowerCount(Long userId) {
        return followRepository.countFollowersByUserId(userId);
    }

    // 유저의 팔로우 수 조회
    @Transactional(readOnly = true)
    public int getFollowingCount(Long userId) {
        return followRepository.countFollowingByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Follow> findByFollower(User follower) {
        return followRepository.findByFollower(follower);
    }

    // 탈퇴할 유저의 팔로워 및 팔로우 삭제
    @Transactional
    public void deleteFollowsByUser(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);

        // 유저가 팔로우한 정보 삭제
        for (Follow follow : follows) {
            followRepository.delete(follow);
        }

        // 유저를 팔로우한 정보 삭제
        follows = followRepository.findByFolloweeId(userId);
        for (Follow follow : follows) {
            followRepository.delete(follow);
        }
    }
}
