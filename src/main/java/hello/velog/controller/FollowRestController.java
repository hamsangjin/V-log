package hello.velog.controller;

import hello.velog.domain.User;
import hello.velog.service.FollowService;
import hello.velog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vlog")
@RequiredArgsConstructor
public class FollowRestController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/follow")
    public ResponseEntity<?> handleFollow(@RequestParam String username, HttpServletRequest request) {
        User follower = userService.getSessionUser(request);
        if (follower == null) return ResponseEntity.status(403).body("{\"error\": \"User must be logged in\"}");

        User followed = userService.findByUsername(username);
        boolean isFollowing = followService.toggleFollow(follower, followed);

        return ResponseEntity.ok("{\"status\": \"" + (isFollowing ? "Following" : "Unfollowing") + "\"}");
    }

    @GetMapping("/follow/status")
    public ResponseEntity<?> checkFollowStatus(@RequestParam String username, HttpServletRequest request) {
        User follower = userService.getSessionUser(request);
        if (follower == null) return ResponseEntity.status(401).body("{\"status\": \"Not Logged In\"}");

        User followed = userService.findByUsername(username);
        boolean isFollowing = followService.isFollowing(follower, followed);
        return ResponseEntity.ok("{\"following\": " + isFollowing + "}");
    }

    @GetMapping("/follow/counts")
    public ResponseEntity<?> getFollowCounts(@RequestParam String username) {
        User user = userService.findByUsername(username);
        int followerCount = followService.getFollowerCount(user.getId());
        int followingCount = followService.getFollowingCount(user.getId());

        Map<String, Integer> counts = new HashMap<>();
        counts.put("followerCount", followerCount);
        counts.put("followingCount", followingCount);

        return ResponseEntity.ok(counts);
    }
}